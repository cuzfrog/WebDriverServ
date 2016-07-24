package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.{Actor, ActorSystem, Props, Terminated}
import akka.io.IO
import boopickle.Default._
import com.github.cuzfrog.webdriver.Messages.{Failed, Request}
import com.typesafe.scalalogging.LazyLogging
import spray.can.Http
import spray.http.{HttpMethods, HttpRequest, HttpResponse, Uri}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

private[webdriver] object Server extends App with LazyLogging {
  private val hostExtractor = "-host=([\d\w\\.]+)".r
  private val host = args.find(_.startsWith("-host=")) match {
    case Some(hostExtractor(h)) => h
    case _ => "localhost"
  }
  private val portExtractor = "-port=([\d]+)".r
  private val port = args.find(_.startsWith("-port=")) match {
    case Some(portExtractor(h)) => h.toInt
    case _ => 90001
  }

  private implicit lazy val system = ActorSystem("WebDriverServ")
  private lazy val handler = system.actorOf(Props[Service], name = "handler")

  IO(Http) ! Http.Bind(handler, interface = host, port = port)

  while(true){
    scala.io.StdIn.readLine().toLowerCase() match{
      case "exit" | "quit" |"shutdown" => shutdown()
      case _ => //do nothing.
    }
  }

  private[webdriver] def shutdown():Unit={
    IO(Http) ! Http.Unbind
    val terminated = system.terminate()
    val f = new PartialFunction[Terminated, Unit] {
      def apply(t: Terminated) = logger.info(s"Actor system terminated: $t")
      def isDefinedAt(t: Terminated) = t.existenceConfirmed
    }
    import system.dispatcher
    terminated.onSuccess(f)
    Await.result(terminated,15 seconds)
  }
}

private[webdriver] class Service extends Actor {
  def receive = {
    case HttpRequest(HttpMethods.GET, Uri.Path("/tell"), _, entity, _) =>
      val request = Unpickle[Request].fromBytes(ByteBuffer.wrap(entity.data.toByteArray))
      val response = try {
        request.execute(ServerApi)
      } catch {
        case e: Exception => Failed(e.getMessage)
      }
      sender ! HttpResponse(entity = Pickle.intoBytes(response).array())
  }
}