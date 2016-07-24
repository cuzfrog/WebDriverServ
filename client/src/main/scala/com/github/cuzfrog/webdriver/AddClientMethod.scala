package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.Messages._
import com.github.cuzfrog.webdriver.WebDriverClient.ask
import com.typesafe.scalalogging.LazyLogging


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
  def kill(): Unit = ask[Success](Kill(driver)).foreach(f => logger.debug(f.msg))
  /**
    * Clean all associated elements in server repository.
    *
    * @return number of elements cleaned.
    */
  def clean(): Unit = ask[Success](Clean(driver)).foreach(f => logger.debug(f.msg))
}
trait FindElementMethod {
  protected val _id: Long
  protected val host: String
  /**
    * Invoke FindElement on the server and return a stub of an element.
    * If this element is a frame, methods are invoked after an automatic driver switch.
    * @param attr which includes:id, name, tag, xpath, class/className, css/cssSelector, link and partialLink. Case insensitive.
    * @param value of the attr.
    * @return An Option of an element.
    */
  def findElement(attr: String, value: String): Option[ClientElement] =
    ask[Ready[Element]](FindElement(_id, attr, value)).map(r => ClientElement(r.data, host))
  /**
    * Invoke associated method on the server and return a sequence of stubs.
    * @param attr which includes:id, name, tag, xpath, class/className, css/cssSelector, link and partialLink. Case insensitive.
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

case class ClientElement(element: Element, host: String) extends FindElementMethod with LazyLogging {
  protected val _id = element._id

  /**
    * Send keys to the element. May fail.
    * @param keys
    */
  def sendKeys(keys: String): Unit = ask[Success](SendKeys(element, keys)).foreach(f => logger.debug(f.msg))
  /**
    * If this causes a web reload or refresh, reference to elements before this invocation will be invalid.
    */
  def submit(): Unit = ask[Success](Submit(element)).foreach(f => logger.debug(f.msg))
  /**
    * If this causes a web reload or refresh, reference to elements before this invocation will be invalid.
    */
  def click(): Unit = ask[Success](Click(element)).foreach(f => logger.debug(f.msg))
  /**
    * Get value of the attribute.
    */
  def getAttr(attr: String): Option[String] = ask[Success](GetAttr(element, attr)).map(_.msg)
  /**
    * Get text of this element.
    */
  def getText:Option[String] = ask[Success](GetText(element)).map(_.msg)

}

