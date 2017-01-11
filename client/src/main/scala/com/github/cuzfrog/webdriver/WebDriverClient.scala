package com.github.cuzfrog.webdriver

import akka.actor.{ActorSystem, Terminated}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{Await, Future, TimeoutException}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import akka.pattern.ask

import scala.reflect.ClassTag

object WebDriverClient extends AddClientMethod with LazyLogging {
  private val config = ConfigFactory.load.withFallback(ConfigFactory.load("reference.conf"))
  private val host = config.getString("webdriver.client.server-uri")
  private val timeoutSec = config.getInt("webdriver.client.timeout")
  private val actionIntervalMs = config.getInt("webdriver.client.action-interval")
  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(timeoutSec seconds)
  private val remoteListener = system.actorSelection(s"akka.tcp://WebDriverServ@$host/user/handler")

  logger.debug(s"WebDriverClient started with configs:host:$host,timeout:$timeoutSec,actionInterval:$actionIntervalMs")

  def shutdownClient(): Future[Terminated] = system.terminate()

  // implicit execution context
  private[webdriver] def control(request: Request): Option[Response] = try {

    Thread.sleep(actionIntervalMs)
    val tcpResponse = (remoteListener ? request).mapTo[Response]
    val response = Await.result(tcpResponse, timeoutSec seconds)

    response match {
      case Failed(msg, r) =>
        logger.debug(s"Server: failed request:$r exception msg:${System.lineSeparator}$msg"); None
      case _ => Some(response)
    }

  } catch {
    case e: TimeoutException =>
      logger.debug("Server connection time out.")
      None
    case e: Exception =>
      logger.debug(e.getMessage)
      None
  }

  private[webdriver] def bounceTest[T: ClassTag](msg: T): T = {
    def implicitPrint(implicit ev: ClassTag[_]) =
      logger.debug(s"Msg's ClassTag runtime class:${ev.runtimeClass}")
    implicitPrint //println runtime class
    val tcpResponse = (remoteListener ? msg).mapTo[T]
    Await.result(tcpResponse, timeoutSec seconds)
  }
}