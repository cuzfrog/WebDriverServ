package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import boopickle.Default._
import com.github.cuzfrog.webdriver.Messages._
import spray.can.Http
import spray.http._

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps

object WebDriverClient {

  def retrieveDriver(host: String, name: String): Driver = ask(RetrieveDriver(name))(host)
  def newDriver(name: String, host: String, typ: DriverTypes.DriverType): Driver = ask(NewDriver(name, typ))(host)
  implicit class ClientDriver(driver: Driver) {
    def getWindows: Seq[Window] = ask(GetWindows(driver))
    def getWindow: Window = ask(GetWindow(driver))
    /**
      * Kill driver on the server, and clean all associated elements in repository. Aka invoke WebDriver.quit().
      */
    def kill(): Unit = ask(Kill(driver))
    /**
      * Clean all associated elements in repository
      * @return number of elements cleaned.
      */
    def clean(): Long = ask(Clean(driver))
  }


  private[webdriver] implicit val system: ActorSystem = ActorSystem("WebDriverCli")
  private[webdriver] implicit val timeout: Timeout = Timeout(15 seconds)

  // implicit execution context
  private[webdriver] def ask[T](message: Messages.Message)(implicit host: String): T = try {
    val data = Pickle.intoBytes(message)
    val response = (IO(Http) ? HttpRequest(method = HttpMethods.POST, uri = Uri(s"$host/tell"), entity = HttpEntity(data.array()))).mapTo[HttpResponse]
    val result = Await.result(response, 15 seconds)
    Unpickle[T].fromBytes(ByteBuffer.wrap(result.entity.data.toByteArray))
  }
}