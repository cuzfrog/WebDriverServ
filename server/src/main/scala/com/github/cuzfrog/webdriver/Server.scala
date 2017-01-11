package com.github.cuzfrog.webdriver

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

private[webdriver] object Server extends App with LazyLogging {
  System.setProperty("config.file", "./application.conf")
  private val config = ConfigFactory.load
  System.setProperty("webdriver.chrome.driver", config.getString("webdriver.chrome.driver"))
  System.setProperty("webdriver.ie.driver", config.getString("webdriver.ie.driver"))
  private val host = config.getString("akka.remote.netty.tcp.hostname")
  private val port = config.getInt("akka.remote.netty.tcp.port")

  private lazy val system = ActorSystem("WebDriverServ")
  private lazy val handler = system.actorOf(Props[Handler], name = "handler")
  handler ! s"Server Initiation: Remoting now listens on addresses: [akka.tcp://${system.name}@$host:$port]"

  private[webdriver] lazy val api = new ServerApi with ServerApiLogAfter

  import system.dispatcher

  private[webdriver] def shutdown(): Unit = {
    logger.info("Server is shutting down...")
    api.shutdown()
    val terminated = system.terminate()
    val f = new PartialFunction[Terminated, Unit] {
      def apply(t: Terminated): Unit = logger.info(s"Actor system terminated: $t")
      def isDefinedAt(t: Terminated): Boolean = t.existenceConfirmed
    }
    terminated.onSuccess(f)
    Await.result(terminated, 15 seconds)
  }
}

private[webdriver] class Handler extends Actor with LazyLogging {
  def receive:Receive = {
    case r: Request =>
      //logger.debug(s"Receive request: $r")
      val response: Response = try {
        r.execute(Server.api)
      } catch {
        case e: Exception =>
          logger.debug(s"Response Failed with exception:$e")
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