package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import boopickle.Default._
import com.github.cuzfrog.webdriver.Messages._
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.http._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object WebDriverClient extends AddClientMethod with LazyLogging {



  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(15 seconds)

  // implicit execution context
  private[webdriver] def ask[T <: Response](message: Messages.Message)(implicit host: String): Option[T] = try {
    val data = Pickle.intoBytes(message)
    val httpResponse = (IO(Http) ? HttpRequest(method = HttpMethods.POST, uri = Uri(s"$host/tell"), entity = HttpEntity(data.array()))).mapTo[HttpResponse]
    val result = Await.result(httpResponse, 15 seconds)
    val response = Unpickle[T].fromBytes(ByteBuffer.wrap(result.entity.data.toByteArray))
    response match {
      case Result(false, msg) => logger.debug(s"Server: failed-$msg"); None
      case _ => Some(response)
    }

  } catch {
    case e: Exception =>
      logger.debug(e.getMessage)
      None
  }
}