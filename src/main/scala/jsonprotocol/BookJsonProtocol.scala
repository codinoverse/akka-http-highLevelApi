package com.codinoverse
package jsonprotocol

import com.codinoverse.model.Book
import spray.json._

trait BookJsonProtocol extends DefaultJsonProtocol {

  implicit val bookFormat = jsonFormat4(Book.apply)

}
