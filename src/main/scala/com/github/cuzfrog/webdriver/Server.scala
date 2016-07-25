package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import akka.io.IO
import boopickle.Default._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.http._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

private[webdriver] object Server extends App with LazyLogging {
  System.setProperty("config.file", "./application.conf")
  lazy val config = ConfigFactory.load()

  private implicit lazy val system = ActorSystem("WebDriverServ")

  private implicit val executionContext= system.dispatcher

  private lazy val handler = system.actorOf(Props[Service], name = "handler")

  IO(Http) ! Http.Bind(handler, interface = config.getString("host"), port = config.getInt("port"))


//  while (true) {
//    val input = scala.io.StdIn.readLine()
//    input match {
//      case "exit" | "quit" | "shutdown" => shutdown()
//      case c => println(s"Bad command:$c")
//    }
//  }

  private[webdriver] def shutdown(): Unit = {
    IO(Http) ! Http.Close
    IO(Http) ! Http.Unbind
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
  implicit val bodyPickler = compositePickler[Response]
    .addConcreteType[Failed]
    .addConcreteType[Success]
    .addConcreteType[Ready[Window]]
    .addConcreteType[Ready[Seq[Window]]]
    .addConcreteType[Ready[Element]]
    .addConcreteType[Ready[Seq[Element]]]
    .addConcreteType[Ready[Driver]]

  def receive = {
    // when a new connection comes in we register ourselves as the connection handler
    case _: Http.Connected => sender ! Http.Register(self)
    case Timedout(HttpRequest(method, uri, _, _, _)) =>
      sender ! HttpResponse(
        status = 500,
        entity = "The " + method + " request to '" + uri + "' has timed out..."
      )
    case HttpRequest(HttpMethods.POST, Uri.Path("/tell"), _, entity, _) =>
      logger.debug(s"Receive request: ${entity.asString}")
      val request = Unpickle[Request].fromBytes(ByteBuffer.wrap(entity.data.toByteArray))
      val response: Response = try {
        request.execute(ServerApi)
      } catch {
        case e: Exception => Failed(e.getMessage)
      }
      val arr = Array.emptyByteArray
      Pickle.intoBytes(response).get(arr)
      sender ! HttpResponse(entity = arr)
    case HttpRequest(HttpMethods.POST, Uri.Path("/test"), _, entity, _)  =>
      logger.debug(s"Receive test request: ${entity.asString}")
      sender ! HttpResponse(entity = s"test response:$entity")
  }
}