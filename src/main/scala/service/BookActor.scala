package com.codinoverse
package service

import akka.actor.{Actor, ActorLogging}
import com.codinoverse.model.Book
import BookActor.{currentBookId, _}

class BookActor extends Actor with ActorLogging{
  override def receive: Receive = {
    case GetAllBooks=>
      log.info(s"Searching for all the books in boostoreDb")
      sender() ! bookStoreDb.values.toList

    case GetBookById(id)=>
      log.info(s"Searching for the book with id :$id")
      sender() ! bookStoreDb.get(id)

    case CreateBook(book)=>
      log.info(s"Creating a Book with id $currentBookId")
      bookStoreDb=bookStoreDb +(currentBookId->book)
      sender() ! BookCreated(currentBookId)
      currentBookId=currentBookId+1

    case ModifyStock(id,quant)=>
      val book = bookStoreDb.get(id)
     val newBook= book.map{
       case Book(name, author, publishedYear, quantity)=>Book(name, author, publishedYear, quantity+quant)
      }
      newBook.foreach(book=>bookStoreDb=bookStoreDb+(id->book))
      sender() ! newBook

    case SearchBookInStock(instock)=>
      log.info(s"searching for the books in stock or not")
      if(instock)
        sender() ! bookStoreDb.values.filter(book=>book.quantity > 0)
        else
        sender() ! bookStoreDb.values.filter(book=>book.quantity == 0)


    case DeleteBook(id) =>
      log.info(s"Searcing for the book with id $id")
      val book = bookStoreDb.get(id)
      book.foreach(b=>bookStoreDb-(id))
      sender() ! book




  }
}

object BookActor {
  var currentBookId: Int = 0
  var bookStoreDb:Map[Int,Book] = Map()

  case object GetAllBooks
  case class CreateBook(book: Book)
  case class GetBookById(id:Int)
  case class BookCreated(id:Int)
  case class ModifyStock(id:Int,quantity:Int)
  case class SearchBookInStock(instock:Boolean)
  case class DeleteBook(id:Int)
}
