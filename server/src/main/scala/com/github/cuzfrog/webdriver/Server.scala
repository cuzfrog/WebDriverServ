package com.github.cuzfrog.webdriver

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

private[webdriver] object Server extends App with LazyLogging {

  System.setProperty("webdriver.chrome.driver", ServConfig.chromeDriverPath)
  System.setProperty("webdriver.ie.driver", ServConfig.IEDriverPath)

  private lazy val system = ActorSystem("WebDriverServ")
  private lazy val handler = system.actorOf(Props[Handler], name = "handler")
  handler ! s"Server Initiation: Remoting now listens on addresses: [akka://${system.name }@${ServConfig.host }:${ServConfig.port }]"

  private[webdriver] lazy val api = new ServerApi with ServerApiLogAfter

  import system.dispatcher

  /**
    * Note: this method should not be called directly from Server, because
    * it only terminates actor system and does not close web drivers.
    * <br>
    * Call Server.shutDown instead if a completed shutting down is required.
    */
  private[webdriver] def terminateActorSystem(): Unit = {
    logger.info("Server is shutting down...")
    val terminated = system.terminate()
    val f = new PartialFunction[Terminated, Unit] {
      def apply(t: Terminated): Unit = logger.info(s"Actor system terminated: $t")
      def isDefinedAt(t: Terminated): Boolean = t.existenceConfirmed
    }
    terminated.onSuccess(f)
    Await.result(terminated, 15 seconds)
  }

  /**
    * Shut down the server. Quit all web driver, clear caches and terminate actor system.
    */
  private[webdriver] def shutDown(): Unit = api.shutdown()
}

private[webdriver] class Handler extends Actor with LazyLogging {
  def receive: Receive = {
    case r: Request =>
      logger.trace(s"Receive request: $r")
      val response: Response = try {
        r.execute(Server.api)
      } catch {
        case e: Exception =>
          logger.debug(s"Response Failed with exception:$e")
          e.printStackTrace()
          Failed(e.getMessage, r)
      }
      sender ! response
    case s: String =>
      logger.info(s"[bounced]$s.")
      sender ! s"[bounced]$s."
    case other =>
      logger.info(s"Unknow msg received:[$other]")
    //sender ! other //send back
  }
}