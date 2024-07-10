package com.codinoverse
package api

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpRequest, StatusCodes}
import akka.pattern.ask
import akka.stream.Materializer
import akka.util.Timeout
import com.codinoverse.model.Book
import com.codinoverse.service.BookActor
import com.codinoverse.service.BookActor.{BookCreated, CreateBook, DeleteBook, GetAllBooks, GetBookById, ModifyStock, SearchBookInStock}
import akka.http.scaladsl.server.Directives._
import com.codinoverse.jsonprotocol.BookJsonProtocol
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}


object HighLevelServerApi extends BookJsonProtocol with SprayJsonSupport{

  implicit val system = ActorSystem("highlevelserver")
  implicit val materializer = Materializer
  implicit val dispatcher = system.dispatcher
  implicit val timeout = Timeout(3.seconds)

  val bookActor = system.actorOf(Props[BookActor],"bookActor")

  val booksList = List(
    Book("Scala Programming", "David Miller", 2005, 2),
      Book("Java and Basics", "S.Chand", 2018, 8),
      Book("Mathematics-1", "R.S.Agarwal", 2004, 9),
      Book("Harry Potter Part -1", "J.K.Rowling", 1999, 0),

  )

  val booksFuture = booksList.map{
    book=>
      (bookActor ? CreateBook(book)).mapTo[BookCreated].map(created=>{
        println(s"Book is Created ${created.id}")
      })
  }

  Future.sequence(booksFuture).onComplete{
    case Success(value) => println("All Books are created")
    case Failure(exception) => println(exception.getMessage)
  }


  val requestHandler =
    pathPrefix("api" / "book") {
      (path("inventory" / Segment) & get) { instock =>
        val futureListBook = (bookActor ? SearchBookInStock(instock.toBoolean)).mapTo[List[Book]]
        onComplete(futureListBook) {
          case Success(books) => complete(StatusCodes.OK, books)
          case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
        }
      } ~
        (parameter(Symbol("id").as[Int]) | path(IntNumber)) { id =>
          get {
            val futureOptionBook = (bookActor ? GetBookById(id)).mapTo[Option[Book]]
            onComplete(futureOptionBook) {
              case Success(Some(book)) => complete(StatusCodes.OK, book)
              case Success(None) => complete(StatusCodes.NotFound, s"Book with ID $id not found")
              case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          } ~
            delete {
              val futureOptionBook = (bookActor ? DeleteBook(id)).mapTo[Option[Book]]
              onComplete(futureOptionBook) {
                case Success(Some(_)) => complete(StatusCodes.OK, s"Book with ID $id deleted")
                case Success(None) => complete(StatusCodes.NotFound, s"Book with ID $id not found")
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
              }
            }
        } ~ pathEndOrSingleSlash {
        get {
          val futureBooklist = (bookActor ? GetAllBooks).mapTo[List[Book]]
          onComplete(futureBooklist) {
            case Success(books) => complete(StatusCodes.OK, books)
            case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
          }
        }
      }
    } ~
      pathPrefix("api" / "book") {
        (post & path("inventory")) {
          (path(IntNumber / IntNumber) | parameter(Symbol("id").as[Int], Symbol("quantity").as[Int])) { (id, quantity) =>
            val futureOptionBook = (bookActor ? ModifyStock(id, quantity)).mapTo[Option[Book]]
            onComplete(futureOptionBook) {
              case Success(Some(book)) => complete(StatusCodes.OK, book)
              case Success(None) => complete(StatusCodes.NotFound, s"Book with ID $id not found")
              case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
            }
          }
        } ~
          post {
            entity(as[Book]) { book =>
              val futureResponse = (bookActor ? CreateBook(book)).mapTo[BookCreated]
              onComplete(futureResponse) {
                case Success(status) => complete(s"Book is Created with id ${status.id}")
                case Failure(ex) => complete(StatusCodes.InternalServerError, s"An error occurred: ${ex.getMessage}")
              }
            }
          }
      }



    }





