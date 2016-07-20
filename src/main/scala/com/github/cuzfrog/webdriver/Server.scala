package com.github.cuzfrog.webdriver

import akka.actor.ActorSystem
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply
import spray.routing.SimpleRoutingApp

object Server extends App with SimpleRoutingApp {
  implicit val system = ActorSystem("WebDriverServ")

  startServer(interface = "localhost", port = 90001) {
    post {
      path("tell") { ctx =>
        complete {
          println(s"recieve request. $ctx")
          Router.response(ctx.request.entity.data.asString)
        }
      }
    }
  }

}