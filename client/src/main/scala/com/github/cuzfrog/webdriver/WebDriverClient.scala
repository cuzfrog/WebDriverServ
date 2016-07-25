package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.io.IO
import akka.util.Timeout
import boopickle.Default._
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.http._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object WebDriverClient extends AddClientMethod with LazyLogging {

  private implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private implicit val timeout: Timeout = Timeout(15 seconds)
  implicit val bodyPickler = compositePickler[Response]
    .addConcreteType[Failed]
    .addConcreteType[Success]
    .addConcreteType[Ready[Window]]
    .addConcreteType[Ready[Seq[Window]]]
    .addConcreteType[Ready[Element]]
    .addConcreteType[Ready[Seq[Element]]]
    .addConcreteType[Ready[Driver]]


  // implicit execution context
  private[webdriver] def ask(message: Request)(implicit host: String): Option[Response] = try {
    val data = Pickle.intoBytes(message)
    import akka.pattern.ask
    val httpResponse = (IO(Http) ? HttpRequest(method = HttpMethods.POST, uri = Uri(s"$host/tell"), entity = HttpEntity(data.array()))).mapTo[HttpResponse]
    val result = Await.result(httpResponse, 15 seconds)
    val response = Unpickle[Response].fromBytes(ByteBuffer.wrap(result.entity.data.toByteArray))
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