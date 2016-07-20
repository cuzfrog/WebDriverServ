package com.github.cuzfrog.webdriver

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http.HttpMethods
import spray.http.HttpRequest
import spray.http.HttpResponse
import spray.http.Uri
import spray.http.HttpEntity

object Client {

  implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  implicit val timeout: Timeout = Timeout(15 seconds)
  // implicit execution context

  def tell(message: Message)(implicit host: String): Future[HttpResponse] = {
    implicit val pickler = Message.pickler
    val data = boopickle.Default.Pickle.intoBytes(message)

    (IO(Http) ? HttpRequest(method = HttpMethods.POST, uri = Uri(s"$host/tell"), entity = HttpEntity(data.array()))).mapTo[HttpResponse]
  }
}