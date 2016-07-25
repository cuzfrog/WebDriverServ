package com.github.cuzfrog.webdriver

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.AskableActorRef
import akka.util.Timeout
import spray.can.Http
import spray.http._
import utest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by Cause Frog on 7/25/2016.
  */
object HttpTest extends TestSuite {
  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(15 seconds)

  val tests = this {
    'httpRequest {
      val httpListener = new AskableActorRef(IO(Http))
      val response=(httpListener ? HttpRequest(method = HttpMethods.POST, uri = Uri(s"http://localhost:60001/test"), entity = HttpEntity("httpTest1"))).mapTo[HttpResponse]
      val result=Await.result(response,15 seconds)
      println(result)
    }
  }
}
