package com.github.cuzfrog.webdriver

sealed trait Message {
  val name = this.getClass.getSimpleName
}

case class NewDriver(driver: String) extends Message

case class RetreiveDriver(id: Long) extends Message

case class FindElement(attr: String, value: String) extends Message

case class SendKeys(element: Element, keys: String) extends Message

case class Submit(element: Element) extends Message

case class SwitchTo(window: Window) extends Message