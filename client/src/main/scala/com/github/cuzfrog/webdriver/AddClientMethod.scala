package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.DriverTypes.DriverType
import com.github.cuzfrog.webdriver.WebDriverClient.control
import com.typesafe.scalalogging.LazyLogging

private[webdriver] trait AddClientMethod extends LazyLogging {
  /**
    * Create a driver instance on the server and return a stub for manipulation.
    *
    * @param name    to give the driver a name, so that it can be easily remembered or retrieved next run time.
    * @param typ     driver's type. See Selenium WebDriver's document. { @see DriverTypes.DriverType}
    * @param waitSec seconds to wait implicitly.
    * @return An Option of a client side driver class with necessary identification and interaction methods.
    */
  def newDriver(name: String, typ: DriverType, waitSec: Int = 15): Option[ClientDriver] =
    control(NewDriver(name, typ, waitSec)) collect { case r: Ready[Driver]@unchecked => ClientDriver(r.data) }
  /**
    * Retrieve the stub of the driver instance from the server.
    *
    * @param name the name of the driver.
    * @return An Option of a client side driver class with necessary identification and interaction methods.
    */
  def retrieveDriver(name: String): Option[ClientDriver] =
    control(RetrieveDriver(name)) collect { case r: Ready[Driver]@unchecked => ClientDriver(r.data) }

  /**
    * Send shutdown command to the server.
    */
  def shutdownServer(): Unit = control(Shutdown) collect { case f: Success => logger.trace(f.msg) }
}

case class ClientDriver(driver: Driver) extends LazyLogging {
  val name = driver.name

  /**
    * @param url to navigate.
    * @return Window focused.
    */
  def navigateTo(url: String): Option[ClientWindow] = control(Navigate(driver, url)) collect { case r: Ready[Window]@unchecked => ClientWindow(r.data) }
  /**
    * Return all windows opened by the driver. Driver will automatically switch to the window on which
    * some method is invoked.
    *
    * @return sequence of windows.
    */
  def getWindows: Seq[ClientWindow] = {
    control(GetWindows(driver)) collect { case r: Ready[Seq[Window]]@unchecked => r.data.map(ClientWindow(_)) } match {
      case Some(s) => s
      case None => Nil
    }
  }
  /**
    * @return current focused window.
    */
  def getWindow: Option[ClientWindow] = control(GetWindow(driver)) collect { case r: Ready[Window]@unchecked => ClientWindow(r.data) }
  /**
    * Kill driver on the server, and clean all associated elements in repository. Aka invoke WebDriver.quit().
    */
  def kill(): Unit = control(Kill(driver)) collect { case f: Success => logger.trace(f.msg) }
  /**
    * Clean all associated elements in server repository.
    *
    * @return number of elements cleaned.
    */
  def clean(): Unit = control(Clean(driver)) collect { case f: Success => logger.trace(f.msg) }
}
private[webdriver] trait WebBodyMethod {
  protected val webBody: WebBody
  /**
    * Invoke FindElement on the server and return a stub of an element.
    * If this element is a frame, methods are invoked after an automatic driver switch.
    *
    * @param attr  which includes:id, name, tag, xpath, class/className, css/cssSelector, link and partialLink. Case insensitive.
    * @param value of the attr.
    * @return An Option of an element.
    */
  def findElement(attr: String, value: String): Option[ClientElement] =
    control(FindElement(webBody, attr, value)) collect { case r: Ready[Element]@unchecked => ClientElement(r.data) }
  /**
    * Invoke associated method on the server and return a sequence of stubs.
    *
    * @param attr  which includes:id, name, tag, xpath, class/className, css/cssSelector, link and partialLink. Case insensitive.
    * @param value of the attr.
    * @return An Seq of elements, when empty it be Nil.
    */
  def findElements(attr: String, value: String): Seq[ClientElement] = {
    control(FindElements(webBody, attr, value)) collect { case r: Ready[Seq[Element]]@unchecked => r.data.map(ClientElement(_)) } match {
      case Some(s) => s
      case None => Nil
    }
  }
  /**
    * Check if an element exists. This method is different from findElement for it does not wait for the the element.
    * @param attr  which includes:id, name, tag, xpath, class/className, css/cssSelector, link and partialLink. Case insensitive.
    * @param value of the attr.
    * @return true if the element is present at this moment. Otherwise false, including error.
    */
  def checkElementExistence(attr: String, value: String): Boolean = {
    control(CheckElementExistence(webBody, attr, value)) match {
      case Some(r: Ready[Boolean]@unchecked) => r.data
      case _ => false
    }
  }
  /**
    * Change type of driver on server to JavascriptExecutor and execute the js.
    * The driver has already switched to this frame or window.
    * This can also be used to inject script.
    */
  def executeJS(script: String, args: AnyRef*): Any = control(ExecuteJS(webBody, script, args))
}
case class ClientWindow(window: Window) extends WebBodyMethod {
  val title = window.title
  /**
    * Equal with windowHandle of the driver on server.
    */
  val handle = window.handle
  protected val webBody: WebBody = window
}

case class ClientElement(element: Element) extends WebBodyMethod with LazyLogging {
  protected val webBody: WebBody = element

  /**
    * Send keys to the element. May fail.
    */
  def sendKeys(keys: String): Unit = control(SendKeys(element, keys)) collect { case f: Success => logger.trace(f.msg) }
  /**
    * If this causes a web reload or refresh, reference to elements before this invocation will be invalid.
    */
  def submit(): Unit = control(Submit(element)) collect { case f: Success => logger.trace(f.msg) }
  /**
    * If this causes a web reload or refresh, reference to elements before this invocation will be invalid.
    */
  def click(): Unit = control(Click(element)) collect { case f: Success => logger.trace(f.msg) }
  /**
    * Get value of the attribute.
    */
  def getAttr(attr: String): Option[String] = control(GetAttr(element, attr)) collect { case f: Success => f.msg }
  /**
    * Get text of this element.
    */
  def getText: Option[String] = control(GetText(element)) collect { case f: Success => f.msg }

}



