package com.github.cuzfrog.webdriver

import org.openqa.selenium.{WebDriver, WebElement}

private[webdriver] sealed trait Container {
  val driver: Driver
}
private[webdriver] case class DriverContainer(driver: Driver, seleniumDriver: WebDriver) extends Container {
  val elements = scala.collection.mutable.ArrayBuffer.empty[Long]
}
private[webdriver] case class ElementContainer(element: Element, seleniumElement: WebElement) extends Container {
  val driver = element.driver
}
private[webdriver] case class WindowContainer(window: Window, seleniumDriver: WebDriver) extends Container {
  val driver = window.driver
}