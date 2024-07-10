package com.codinoverse

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import com.codinoverse.api.HighLevelServerApi._

import scala.io.StdIn

 object CodinoverseBookStoreApp extends App{


  val bindingServer = Http()
    .newServerAt("localhost",8080)
    .bind(requestHandler)

  StdIn.readLine()

  bindingServer
    .flatMap(_.unbind())
    .onComplete(_=>system.terminate())

}
