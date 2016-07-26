package com.github.cuzfrog.webdriver

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object WebDriverClient extends AddClientMethod with LazyLogging {

  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(15 seconds)

  def shutdownClient() = system.terminate()

  // implicit execution context
  private[webdriver] def ask(request: Request)(implicit host: String): Option[Response] = try {

    val remoteListener = system.actorSelection(s"akka.tcp://WebDriverServ@$host/user/handler")

    val tcpResponse = (remoteListener ? request).mapTo[Response]
    val response = Await.result(tcpResponse, 15 seconds)

    response match {
      case Failed(msg) => logger.debug(s"Server: failed-$msg"); None
      case _ => Some(response)
    }

  } catch {
    case e: Exception =>
      logger.debug(e.getMessage)
      None
  }
}