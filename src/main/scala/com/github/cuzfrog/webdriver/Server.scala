package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import akka.io.IO
import boopickle.Default._
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.http.{HttpMethods, HttpRequest, HttpResponse, Uri}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

private[webdriver] object Server extends App with LazyLogging {
  System.setProperty("config.file","./application.conf")
  val config=ConfigFactory.load()

  private implicit lazy val system = ActorSystem("WebDriverServ")
  private lazy val handler = system.actorOf(Props[Service], name = "handler")

  IO(Http) ! Http.Bind(handler, interface = config.getString("host"), port = config.getInt("port"))

  while (true) {
    scala.io.StdIn.readLine().toLowerCase() match {
      case "exit" | "quit" | "shutdown" => shutdown()
      case _ => //do nothing.
    }
  }

  private[webdriver] def shutdown(): Unit = {
    IO(Http) ! Http.Unbind
    val terminated = system.terminate()
    val f = new PartialFunction[Terminated, Unit] {
      def apply(t: Terminated) = logger.info(s"Actor system terminated: $t")
      def isDefinedAt(t: Terminated) = t.existenceConfirmed
    }
    import system.dispatcher
    terminated.onSuccess(f)
    Await.result(terminated, 15 seconds)
  }
}

private[webdriver] class Service extends Actor {
  implicit val bodyPickler = compositePickler[Response]
    .addConcreteType[Failed]
    .addConcreteType[Success]
    .addConcreteType[Ready[Window]]
    .addConcreteType[Ready[Seq[Window]]]
    .addConcreteType[Ready[Element]]
    .addConcreteType[Ready[Seq[Element]]]
    .addConcreteType[Ready[Driver]]

  def receive = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/tell"), _, entity, _) =>
      val request = Unpickle[Request].fromBytes(ByteBuffer.wrap(entity.data.toByteArray))
      val response: Response = try {
        request.execute(ServerApi)
      } catch {
        case e: Exception => Failed(e.getMessage)
      }
      val arr = Array.emptyByteArray
      Pickle.intoBytes(response).get(arr)
      sender ! HttpResponse(entity = arr)
  }
}