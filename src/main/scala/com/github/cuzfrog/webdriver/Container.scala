package com.github.cuzfrog.webdriver

import java.util.concurrent.CopyOnWriteArrayList

import org.openqa.selenium.{By, WebDriver, WebElement}
import Elements._


sealed trait Container {
  val driver: Driver
}
case class DriverContainer(driver: Driver, seleniumDriver: WebDriver) extends Container {
  val elements = scala.collection.mutable.Seq.empty[Long]
}
case class ElementContainer(element: Element, seleniumElement: WebElement) extends Container {
  val driver = element.driver
}
case class WindowContainer(driver: Driver, seleniumDriver: WebDriver, seleniumWindowHandle: String) extends Container