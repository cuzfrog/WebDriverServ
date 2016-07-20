package com.github.cuzfrog.webdriver

import akka.actor.ActorSystem
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import spray.routing.SimpleRoutingApp
import spray.http.HttpResponse
import spray.can.Http
import spray.http.HttpRequest
import akka.actor.Props
import akka.actor.Actor
import spray.http.Uri
import akka.io.IO
import spray.http.HttpMethods

object Server extends App {
  implicit val system = ActorSystem("WebDriverServ")
  val handler = system.actorOf(Props[Service], name = "handler")
  IO(Http) ! Http.Bind(handler, interface = "localhost", port = 90001)

}

class Service extends Actor {
  def receive = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/tell"), _, entity, _) =>
      sender() ! HttpResponse(entity = Router.response(entity.asString))
  }
}