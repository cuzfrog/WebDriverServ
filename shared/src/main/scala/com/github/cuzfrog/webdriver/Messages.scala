package com.github.cuzfrog.webdriver

import Elements._

object Messages {

  sealed trait Message

  sealed trait Request extends Message
  case class NewDriver(name: String, typ: DriverTypes.DriverType) extends Request
  case class RetrieveDriver(name: String) extends Request
  case class Kill(driver: Driver) extends Request
  case class Clean(driver: Driver) extends Request
  case class FindElement(id: Long, attr: String, value: String) extends Request
  case class SendKeys(element: Element, keys: String) extends Request
  case class Submit(element: Element) extends Request
  case class Click(element: Element) extends Request
  case class GetAttr(element: Element, attr: String) extends Request

  sealed trait Response extends Message
  case class Failed(msg: String) extends Response
  case class Success(msg: String) extends Response
  case class ReadyDriver(driver: Driver) extends Response
  case class ReadyWindow(window: Window) extends Response
  case class ReadyElement(element: Element) extends Response
  case class Value(value: String) extends Response

}