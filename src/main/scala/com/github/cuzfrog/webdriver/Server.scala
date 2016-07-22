package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import boopickle.Default._
import com.github.cuzfrog.webdriver.Messages.{Failed, Request, Response}
import spray.can.Http
import spray.http.{HttpMethods, HttpRequest, HttpResponse, Uri}

private[webdriver] object Server extends App {
  private val hostExtractor = "-host=([\d\w\\.]+)".r
  private val host = args.find(_.startsWith("-host=")) match {
    case Some(hostExtractor(h)) => h
    case _ => "localhost"
  }
  private val portExtractor = "-port=([\d]+)".r
  private val port = args.find(_.startsWith("-port=")) match {
    case Some(portExtractor(h)) => h.toInt
    case _ => 90001
  }

  private implicit lazy val system = ActorSystem("WebDriverServ")
  private lazy val handler = system.actorOf(Props[Service], name = "handler")

  IO(Http) ! Http.Bind(handler, interface = host, port = port)

}

private[webdriver] class Service extends Actor {
  def receive = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/tell"), _, entity, _) =>
      val request = Unpickle[Request].fromBytes(ByteBuffer.wrap(entity.data.toByteArray))
      val response = try {
        request.execute(ServerApi)
      } catch {
        case e: Exception => Failed(e.getMessage)
      }
      sender ! HttpResponse(entity = Pickle.intoBytes(response).array())
  }
}