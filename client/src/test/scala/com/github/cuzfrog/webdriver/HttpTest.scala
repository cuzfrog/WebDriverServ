package com.github.cuzfrog.webdriver

import akka.actor.Actor.Receive
import akka.actor.{Actor, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

/**
  * Created by Cause Frog on 7/25/2016.
  */
@deprecated("passed")
object HttpTest extends App with LazyLogging {
  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = 5.seconds
  private val clientSender=system.actorOf(Props[ClientSender],"clientSender")
  try {

    clientSender ! "test"
    //val result = Await.result(response, 5 seconds)
    //logger.debug(result.toString)
  } finally {
    Thread.sleep(1000)
    system.terminate()
  }
}
@deprecated("passed")
class ClientSender extends Actor {
  val remote = context.actorSelection("akka.tcp://WebDriverServ@127.0.0.1:60002/user/handler")
  override def receive: Receive = {
    case s: String => remote ! s
  }
}
