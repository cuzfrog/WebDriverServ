package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import boopickle.Default._
import com.github.cuzfrog.webdriver.Messages._

object Router {

  def response(msg: Array[Byte]): Array[Byte] = {
    val response = try {
      routeMessage(Unpickle[Request].fromBytes(ByteBuffer.wrap(msg)))
    } catch {
      case e: Exception => Failed(e.getMessage)
    }
    Pickle.intoBytes(response).array()
  }


  private def routeMessage(request: Request): Response = {
    implicit def extractDriverName(driver: Driver): String = driver.name

    request match {
      case NewDriver(name, typ) => ReadyDriver(ServerApi.newDriver(name, typ))
      case RetrieveDriver(name) => ServerApi.retrieveDriver(name) match {
        case Some(d) => ReadyDriver(d)
        case None => Failed(s"No such driver[$name] on server.")
      }
      case FindElement(id, attr, value) => ServerApi.findElement(id, attr, value) match {
        case Some(d) => ReadyElement(d)
        case None => Failed(s"Cannot find element[attr:$attr|value:$value].")
      }
    }
  }

}
