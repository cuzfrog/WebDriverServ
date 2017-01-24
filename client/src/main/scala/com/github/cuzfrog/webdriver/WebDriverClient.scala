package com.github.cuzfrog.webdriver

import akka.actor.{Actor, ActorRef, ActorSystem, AddressFromURIString, Deploy, Props, Terminated}
import akka.pattern.ask
import akka.remote.RemoteScope
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future, TimeoutException}
import scala.language.postfixOps
import scala.reflect.ClassTag

object WebDriverClient extends AddClientMethod with LazyLogging {
  import ClientConfig.{actionInterval, serverUri, timeoutSec}
  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(timeoutSec seconds)
  private val remoteListener =
    system.actorSelection(s"akka${ClientConfig.akkaProtocol}://WebDriverServ@$serverUri/user/handler")

  logger.debug(s"WebDriverClient started with configs:server:$serverUri,timeout:$timeoutSec,actionInterval:$actionInterval")

  def shutdownClient(): Future[Terminated] = system.terminate()

  // implicit execution context
  private[webdriver] def control(request: Request): Option[Response] = try {

    Thread.sleep(actionInterval)
    val tcpResponse = (remoteListener ? request).mapTo[Response]
    val response = Await.result(tcpResponse, timeoutSec seconds)

    response match {
      case Failed(msg, r) =>
        logger.debug(s"Server: failed request:$r exception msg:${System.lineSeparator}$msg"); None
      case _ => Some(response)
    }

  } catch {
    case e: TimeoutException =>
      logger.debug(s"Server connection time out. Request[$request]")
      None
    case e: Exception =>
      logger.debug(e.getMessage)
      None
  }


  //===================test and experimental=======================
  object ExperimentalAndTest {
    private[webdriver] def bounceTest[T: ClassTag](msg: T): T = {
      def implicitPrint(implicit ev: ClassTag[_]) =
        logger.debug(s"Msg's ClassTag runtime class:${ev.runtimeClass}")
      implicitPrint
      //println runtime class
      val tcpResponse = (remoteListener ? msg).mapTo[T]
      Await.result(tcpResponse, timeoutSec seconds)
    }

    private lazy val remoteAddress = AddressFromURIString(s"akka${ClientConfig.akkaProtocol}://WebDriverServ@$serverUri")
    private[webdriver] def deployActorToServer[T <: Actor : ClassTag](name: String): ActorRef = {
      system.actorOf(Props[T].withDeploy(Deploy(scope = RemoteScope(remoteAddress))),
        name = name)
    }

    private[webdriver] def sendMessageTo(actor: ActorRef, msg: String): String = try {
      Await.result((actor ? msg).mapTo[String], 5 seconds)
    } catch {
      case e: Exception =>
        e.printStackTrace()
        "failed."
    }

    private[webdriver] def sendParseLogic(funcSrcCode: String): Option[String] = {
      control(GetInnerHtml(null, funcSrcCode)) collect { case r: Ready[String]@unchecked => r.data }
    }
  }
}