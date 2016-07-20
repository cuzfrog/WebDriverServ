package com.github.cuzfrog.spatest

import spray.routing.SimpleRoutingApp
import akka.actor.ActorSystem
import spray.http.HttpEntity
import spray.http.MediaTypes
import spray.http.ContentType.apply
import spray.httpx.marshalling.ToResponseMarshallable.isMarshallable
import spray.routing.Directive.pimpApply

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