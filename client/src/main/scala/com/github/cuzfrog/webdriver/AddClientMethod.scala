package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.WebDriverClient.ask
import com.typesafe.scalalogging.LazyLogging

trait AddClientMethod extends LazyLogging {
  /**
    * Create a driver instance on the server and return a stub for manipulation.
    *
    * @param host url of the host, example: http://localhost:9000
    * @param name to give the driver a name, so that it can be easily remembered or retrieved next run time.
    * @param typ  driver's type. See Selenium WebDriver's document. { @see DriverTypes.DriverType}
    * @return An Option of a client side driver class with necessary identification and interaction methods.
    */
  def newDriver(host: String, name: String, typ: DriverTypes.DriverType): Option[ClientDriver] =
    ask(NewDriver(name, typ))(host) collect { case r: Ready[Driver] => ClientDriver(r.data, host) }
  /**
    * Retrieve the stub of the driver instance from the server.
    *
    * @param host url of the host, example: http://localhost:90001
    * @param name the name of the driver.
    * @return An Option of a client side driver class with necessary identification and interaction methods.
    */
  def retrieveDriver(host: String, name: String): Option[ClientDriver] =
    ask(RetrieveDriver(name))(host).map{case r:Ready[Driver] => ClientDriver(r.data, host)}

  /**
    * Send shutdown command to the server.
    *
    * @param host uri of the server.
    */
  def shutdownServer(host: String): Unit = ask[Success](Shutdown)(host).foreach(f => logger.trace(f.msg))
}

case class ClientDriver(driver: Driver, private implicit val host: String) extends LazyLogging {
  val name = driver.name
  /**
    * Return all windows opened by the driver. Driver will automatically switch to the window on which
    * some method is invoked.
    *
    * @return sequence of windows.
    */
  def getWindows: Seq[ClientWindow] = {
    ask[Ready[Seq[Window]]](GetWindows(driver)).map(_.data.map(ClientWindow(_, host))) match {
      case Some(s) => s
      case None => Nil
    }
  }
  /**
    * @return current focused window.
    */
  def getWindow: Option[ClientWindow] = ask[Ready[Window]](GetWindow(driver)).map(r => ClientWindow(r.data, host))
  /**
    * Kill driver on the server, and clean all associated elements in repository. Aka invoke WebDriver.quit().
    */
  def kill(): Unit = ask[Success](Kill(driver)).foreach(f => logger.trace(f.msg))
  /**
    * Clean all associated elements in server repository.
    *
    * @return number of elements cleaned.
    */
  def clean(): Unit = ask[Success](Clean(driver)).foreach(f => logger.trace(f.msg))
}
trait FindElementMethod {
  protected val _id: Long
  protected implicit val host: String
  /**
    * Invoke FindElement on the server and return a stub of an element.
    * If this element is a frame, methods are invoked after an automatic driver switch.
    *
    * @param attr  which includes:id, name, tag, xpath, class/className, css/cssSelector, link and partialLink. Case insensitive.
    * @param value of the attr.
    * @return An Option of an element.
    */
  def findElement(attr: String, value: String): Option[ClientElement] =
    ask[Ready[Element]](FindElement(_id, attr, value)).map(r => ClientElement(r.data, host))
  /**
    * Invoke associated method on the server and return a sequence of stubs.
    *
    * @param attr  which includes:id, name, tag, xpath, class/className, css/cssSelector, link and partialLink. Case insensitive.
    * @param value of the attr.
    * @return An Seq of elements, when empty it be Nil.
    */
  def findElements(attr: String, value: String): Seq[ClientElement] = {
    ask[Ready[Seq[Element]]](FindElements(_id, attr, value)).map(_.data.map(ClientElement(_, host))) match {
      case Some(s) => s
      case None => Nil
    }
  }
}
case class ClientWindow(window: Window, host: String) extends FindElementMethod {
  val title = window.title
  /**
    * Equal with windowHandle of the driver on server.
    */
  val handle = window.handle
  protected val _id = window._id
}

case class ClientElement(element: Element, implicit val host: String) extends FindElementMethod with LazyLogging {
  protected val _id = element._id

  /**
    * Send keys to the element. May fail.
    */
  def sendKeys(keys: String): Unit = ask[Success](SendKeys(element, keys)).foreach(f => logger.trace(f.msg))
  /**
    * If this causes a web reload or refresh, reference to elements before this invocation will be invalid.
    */
  def submit(): Unit = ask[Success](Submit(element)).foreach(f => logger.trace(f.msg))
  /**
    * If this causes a web reload or refresh, reference to elements before this invocation will be invalid.
    */
  def click(): Unit = ask[Success](Click(element)).foreach(f => logger.trace(f.msg))
  /**
    * Get value of the attribute.
    */
  def getAttr(attr: String): Option[String] = ask[Success](GetAttr(element, attr)).map(_.msg)
  /**
    * Get text of this element.
    */
  def getText: Option[String] = ask[Success](GetText(element)).map(_.msg)

}



