package com.github.cuzfrog.webdriver

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

import scala.language.postfixOps

/**
  * Created by Cause Frog on 7/25/2016.
  */
@deprecated("passed","dev")
object HttpTest extends LazyLogging {
//  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
//  private implicit val timeout: Timeout = 5.seconds
//  private val clientSender=system.actorOf(Props[ClientSender],"clientSender")
//  try {
//
//    clientSender ! "test"
//    //val result = Await.result(response, 5 seconds)
//    //logger.debug(result.toString)
//  } finally {
//    Thread.sleep(1000)
//    system.terminate()
//  }
}
@deprecated("passed","dev")
class ClientSender extends Actor {
  val remote = context.actorSelection("akka.tcp://WebDriverServ@127.0.0.1:60002/user/handler")
  override def receive: Receive = {
    case s: String => remote ! s
  }
}
