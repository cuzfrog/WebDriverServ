package com.github.cuzfrog.webdriver

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import spray.can.Http
import spray.http.{HttpMethods, HttpRequest, HttpResponse, Uri}


object Server extends App {
  System.setProperty("webdriver.ie.driver", "C:/program1/Selenium/IEDriverServer.exe")

  implicit lazy val system = ActorSystem("WebDriverServ")
  lazy val handler = system.actorOf(Props[Service], name = "handler")
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = 90001)

}



class Service extends Actor {
  def receive = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/tell"), _, entity, _) =>
      sender() ! HttpResponse(entity = Router.response(entity.data.toByteArray))
  }
}