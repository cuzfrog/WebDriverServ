package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.Elements._

private[webdriver] object Messages {

  sealed trait Message

  sealed trait Request extends Message
  case class NewDriver(name: String, typ: DriverTypes.DriverType) extends Request
  case class RetrieveDriver(name: String) extends Request
  case class Kill(driver: Driver) extends Request
  case class Clean(driver: Driver) extends Request
  case class FindElement(parent_id: Long, attr: String, value: String) extends Request
  case class FindElements(parent_id: Long, attr: String, value: String) extends Request
  case class SendKeys(element: Element, keys: String) extends Request
  case class Submit(element: Element) extends Request
  case class Click(element: Element) extends Request
  case class GetAttr(element: Element, attr: String) extends Request
  case class GetText(element: Element) extends Request
  case class GetWindow(driver: Driver) extends Request
  case class GetWindows(driver: Driver) extends Request

  sealed trait Response extends Message
  case class Failed(msg: String) extends Response
  case class Success(msg: String) extends Response
  case class ReadyDriver(driver: Driver) extends Response
  case class ReadyWindow(window: Window) extends Response
  case class ReadyWindows(windows: Seq[Window]) extends Response
  case class ReadyElement(element: Element) extends Response
  case class ReadyElements(elements: Seq[Element]) extends Response

}