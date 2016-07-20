package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http
import spray.http._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
import boopickle.Default._

object WebDriverClient {

  import Messages._

  def retrieve(host: String, name: String): Driver = ask(RetrieveDriver(name))(host)

  def create(name: String, host: String, typ: DriverTypes.DriverType): Driver = ask(NewDriver(name,typ))(host)

  implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  implicit val timeout: Timeout = Timeout(15 seconds)

  // implicit execution context
  private def ask[T](message: Messages.Message)(implicit host: String): T =try {
    val data = Pickle.intoBytes(message)
    val response=(IO(Http) ? HttpRequest(method = HttpMethods.POST, uri = Uri(s"$host/tell"), entity = HttpEntity(data.array()))).mapTo[HttpResponse]
    val result = Await.result(response, 15 seconds)
    implicit def arrToBuf(arr: Array[Byte]): ByteBuffer = ByteBuffer.wrap(arr)
    Unpickle[T].fromBytes(result.entity.data.toByteArray)
  }
}