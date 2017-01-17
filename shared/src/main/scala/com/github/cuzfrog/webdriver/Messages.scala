package com.github.cuzfrog.webdriver

import scala.language.implicitConversions

private[webdriver] sealed trait Message {
  protected implicit def classNameToString(message: Message): String = message.getClass.getSimpleName
}

private[webdriver] sealed trait Request extends Message {
  def execute(api: Api): Response
}

private[webdriver] case class NewDriver(name: String, typ: DriverTypes.DriverType, waitSec: Int) extends Request {
  override def execute(api: Api): Ready[Driver] = Ready[Driver](api.newDriver(name, typ, waitSec))
}

private[webdriver] case class RetrieveDriver(name: String) extends Request {
  override def execute(api: Api): Response = api.retrieveDriver(name) match {
    case Some(d) => Ready[Driver](d)
    case None => Failed(s"No such driver[$name] on server.", this)
  }
}

private[webdriver] case class Kill(driver: Driver) extends Request {
  override def execute(api: Api): Success = {
    val eleCnt = api.kill(driver)
    Success(s"Driver quit, $eleCnt elements cleaned.")
  }
}

private[webdriver] case class CleanCache(driver: Driver) extends Request {
  override def execute(api: Api): Success = {
    val eleCnt = api.cleanCache(driver)
    Success(s"$eleCnt elements cleaned.")
  }
}

private[webdriver] case class Navigate(driver: Driver, url: String) extends Request {
  override def execute(api: Api): Ready[Window] = {
    val window = api.navigateTo(driver, url)
    Ready(window)
  }
}

private[webdriver] case class GetWindow(driver: Driver) extends Request {
  override def execute(api: Api): Ready[Window] = Ready[Window](api.getWindow(driver))
}

private[webdriver] case class GetWindows(driver: Driver) extends Request {
  override def execute(api: Api): Ready[Seq[Window]] = Ready[Seq[Window]](api.getWindows(driver))
}

private[webdriver] case class FindElement(webBody: WebBody, attr: String, value: String) extends Request {
  override def execute(api: Api): Ready[Element] = Ready[Element](api.findElement(webBody, attr, value))
}

private[webdriver] case class FindElements(webBody: WebBody, attr: String, value: String) extends Request {
  override def execute(api: Api): Ready[Seq[Element]] = Ready[Seq[Element]](api.findElements(webBody, attr, value))
}

private[webdriver] case class FindElementEx(webBody: WebBody, attrPairs: List[(String, String)]) extends Request {
  override def execute(api: Api): Ready[Element] = Ready[Element](api.findElementEx(webBody, attrPairs))
}

private[webdriver] case class CheckElementExistence(webBody: WebBody, attr: String, value: String) extends Request {
  override def execute(api: Api): Ready[Boolean] = Ready[Boolean](api.checkElementExistence(webBody, attr, value))
}

private[webdriver] case class ExecuteJS(webBody: WebBody, script: String, args: AnyRef*) extends Request {
  override def execute(api: Api): Ready[Any] = Ready[Any](api.executeJS(webBody, script))
}

private[webdriver] case class SendKeys(element: Element, keys: String) extends Request {
  override def execute(api: Api): Success = {
    api.sendKeys(element, keys)
    Success("Keys sent.")
  }
}

private[webdriver] case class ClearText(element: Element) extends Request {
  override def execute(api: Api): Success = {
    api.clearText(element)
    Success(this)
  }
}

private[webdriver] case class Submit(element: Element) extends Request {
  override def execute(api: Api): Success = {
    api.submit(element)
    Success(this)
  }
}

private[webdriver] case class Click(element: Element) extends Request {
  override def execute(api: Api): Success = {
    api.click(element)
    Success(this)
  }
}

private[webdriver] case class GetAttr(element: Element, attr: String) extends Request {
  override def execute(api: Api) = Success(api.getAttr(element, attr))
}

private[webdriver] case class GetText(element: Element) extends Request {
  override def execute(api: Api) = Success(api.getText(element))
}

private[webdriver] case class CloseWindow(window: Window) extends Request {
  override def execute(api: Api): Success = {
    api.closeWindow(window)
    Success(this)
  }
}

private[webdriver] case object Shutdown extends Request {
  override def execute(api: Api): Success = {
    api.shutdown()
    Success("Server shutdown.")
  }
}

private[webdriver] case class GetInnerHtml(element: Element,parseLogic: String) extends Request{
  override def execute(api: Api): Response = Ready(api.getInnerHtml(element,parseLogic))
}

private[webdriver] sealed trait Response extends Message
private[webdriver] case class Failed(msg: String, request: Request) extends Response
private[webdriver] case class Success(msg: String) extends Response
private[webdriver] case class Ready[T](data: T) extends Response

