package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import boopickle.Default._
import com.github.cuzfrog.webdriver.Messages.{Request,Response}
import spray.can.Http
import spray.http.{HttpMethods, HttpRequest, HttpResponse, Uri}


object Server extends App {
  System.setProperty("webdriver.ie.driver", "C:/program1/Selenium/IEDriverServer.exe")

  implicit lazy val system = ActorSystem("WebDriverServ")
  lazy val handler = system.actorOf(Props[Service], name = "handler")
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = 90001)

  lazy val router = system.actorOf(Props[Router], name = "router")
}


class Service extends Actor {

  def receive = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/tell"), _, entity, _) =>
      Server.router ! Unpickle[Request].fromBytes(ByteBuffer.wrap(entity.data.toByteArray))
    case r: Response =>
      sender() ! HttpResponse(entity = Pickle.intoBytes(r).array())
  }
}