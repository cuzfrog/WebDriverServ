package com.github.cuzfrog.webdriver

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

/**
  *
  * @param timeoutSec timeout for communication with server.
  *                   This should be set long, because server need time to take web action.
  * @param actionIntervalMs the least time interval between every two request to the server.
  */
final class WebDriverClient(timeoutSec: Int = 15, actionIntervalMs: Int = 50)
  extends AddClientMethod with LazyLogging {

  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(timeoutSec seconds)

  def shutdownClient() = system.terminate()

  // implicit execution context
  private[webdriver] def control(request: Request)(implicit host: String): Option[Response] = try {
    import akka.pattern.ask
    val remoteListener = system.actorSelection(s"akka.tcp://WebDriverServ@$host/user/handler")
    Thread.sleep(actionIntervalMs)
    val tcpResponse = (remoteListener ? request).mapTo[Response]
    val response = Await.result(tcpResponse, timeoutSec seconds)

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