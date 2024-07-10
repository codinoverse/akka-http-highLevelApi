package com.codinoverse

import akka.actor.{ActorRef, Props}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.util.Timeout
import akka.pattern.ask
import com.codinoverse.service.BookActor
import org.mockito.Mockito._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import com.codinoverse.api.HighLevelServerApi
import com.codinoverse.api.HighLevelServerApi.system
import com.codinoverse.jsonprotocol.BookJsonProtocol
import com.codinoverse.model.Book
import com.codinoverse.service.BookActor.GetAllBooks
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class CodinoverBookStoreAppTest extends AnyWordSpec with Matchers with ScalatestRouteTest with BookJsonProtocol with SprayJsonSupport{


  val bookActor = system.actorOf(Props[BookActor],"bookActor")
  implicit val timeout = Timeout(3.seconds)

  val bookRoutes = HighLevelServerApi.requestHandler

  "BookRoutes" should{
    "return all the books from the bookstoreApp" in{

      Get("/api/book")~>bookRoutes~>check{
        status shouldEqual StatusCodes.OK
        responseAs[List[Book]].isInstanceOf[List[Book]]
      }


    }
    "return books in stock for a valid inventory request" in{
      Get("/api/book/inventory/true")~>bookRoutes~>check{
        status shouldEqual StatusCodes.OK
        assert(responseAs[List[Book]].head.quantity>0)
      }
    }
    "return a book for a valid Id" in {
      Get(s"/api/book/1")~>bookRoutes~>check{
        status shouldEqual StatusCodes.OK
        responseAs[Option[Book]].isInstanceOf[Option[Book]]
      }
    }
    "return a book for a valid id query parameter" in {
      Get(s"/api/book?id=1") ~> bookRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Option[Book]].isInstanceOf[Option[Book]]
      }
    }
    "delete a book with id" in {
      Delete(s"/api/book/1")~>bookRoutes~>check{
        status shouldEqual StatusCodes.OK
      }
    }
    "modify stock for a valid book id" in{
      Post(s"/api/book/inventory?id=1&quantity=24") ~> bookRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[Option[Book]].isInstanceOf[Option[Book]]
      }
    }
    "create a book in the book store" in {
      Post(s"/api/book",Book("Programming concepts","Spelkar",2002,98))~>bookRoutes~>check{
        status shouldEqual StatusCodes.OK
      }
    }
  }


}
