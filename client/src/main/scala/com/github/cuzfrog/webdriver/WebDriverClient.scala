package com.github.cuzfrog.webdriver

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps


object WebDriverClient extends AddClientMethod with LazyLogging {
  private val config=ConfigFactory.load
  private val host=config.getString("webdriver.client.host")
  private val timeoutSec=config.getInt("webdriver.client.timeout")
  private val actionIntervalMs=config.getInt("webdriver.client.action-interval")
  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(timeoutSec seconds)
  private val remoteListener = system.actorSelection(s"akka.tcp://WebDriverServ@$host/user/handler")
  def shutdownClient() = system.terminate()

  // implicit execution context
  private[webdriver] def control(request: Request): Option[Response] = try {
    import akka.pattern.ask

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