package com.github.cuzfrog.webdriver

import java.nio.ByteBuffer

import boopickle.Default._
import Messages._
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import Server.drivers

object Router {
  def response(msg: Array[Byte]): Array[Byte] = {
    val responseData = Unpickle[Message].fromBytes(ByteBuffer.wrap(msg)) match {
      case NewDriver(name, typ) => {
        val webDriver = typ match {
          case DriverTypes.IE => new InternetExplorerDriver()
          case DriverTypes.Chrome => new ChromeDriver()
          case DriverTypes.FireFox => new FirefoxDriver()
          case DriverTypes.HtmlUnit => new HtmlUnitDriver()
        }
        drivers.put(name, webDriver)
        ReadyDriver(Driver(name))
      }
      case RetrieveDriver(name) => drivers.get(name) match {
        case Some(d) => ReadyDriver(Driver(name))
        case None => Failed(s"No such driver[$name] on server.")
      }
    }
    Pickle.intoBytes(responseData).array()
  }
}