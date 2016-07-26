package com.github.cuzfrog.webdriver

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

private[webdriver] object Server extends App with LazyLogging {
  System.setProperty("config.file","./application.conf")
  val config=ConfigFactory.load
  System.setProperty("webdriver.chrome.driver", config.getString("webdriver.chrome.driver"))
  System.setProperty("webdriver.ie.driver", config.getString("webdriver.ie.driver"))

  private val system = ActorSystem("WebDriverServ")
  private val handler = system.actorOf(Props[Service], name = "handler")
  import system.dispatcher

  //  while (true) {
  //    val input = scala.io.StdIn.readLine()
  //    input match {
  //      case "exit" | "quit" | "shutdown" => shutdown()
  //      case c => println(s"Bad command:$c")
  //    }
  //  }

  private[webdriver] def shutdown(): Unit = {
    val terminated = system.terminate()
    val f = new PartialFunction[Terminated, Unit] {
      def apply(t: Terminated) = logger.info(s"Actor system terminated: $t")
      def isDefinedAt(t: Terminated) = t.existenceConfirmed
    }
    terminated.onSuccess(f)
    Await.result(terminated, 15 seconds)
  }
}

private[webdriver] class Service extends Actor with LazyLogging {

  def receive = {
    case r: Request =>
      //logger.debug(s"Receive request: $r")
      val response: Response = try {
        r.execute(ServerApi)
      } catch {
        case e: Exception => Failed(e.getMessage)
      }
      sender ! response
    case s:String =>
      logger.debug(s"receive test mes.$s")
      //sender ! "test msg received."
  }
}