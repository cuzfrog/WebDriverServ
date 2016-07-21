package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import boopickle.Default._
import spray.can.Http
import spray.http._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object WebDriverClient {

  import Messages._

  def retrieve(host: String, name: String): Driver = ask(RetrieveDriver(name))(host)

  def create(name: String, host: String, typ: DriverTypes.DriverType): Driver = ask(NewDriver(name,typ))(host)

  private[webdriver] implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private[webdriver] implicit val timeout: Timeout = Timeout(15 seconds)

  // implicit execution context
  private[webdriver] def ask[T](message: Messages.Message)(implicit host: String): T =try {
    val data = Pickle.intoBytes(message)
    val response=(IO(Http) ? HttpRequest(method = HttpMethods.POST, uri = Uri(s"$host/tell"), entity = HttpEntity(data.array()))).mapTo[HttpResponse]
    val result = Await.result(response, 15 seconds)
    implicit def arrToBuf(arr: Array[Byte]): ByteBuffer = ByteBuffer.wrap(arr)
    Unpickle[T].fromBytes(result.entity.data.toByteArray)
  }
}