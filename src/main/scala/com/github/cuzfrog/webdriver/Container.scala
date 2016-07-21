package com.github.cuzfrog.webdriver

import org.openqa.selenium.{By, WebDriver, WebElement}
import Elements._



sealed trait Container
case class DriverContainer(seleniumDriver: WebDriver) extends Container
case class ElementContainer(seleniumElement: WebElement) extends Container
case class WindowContainer(seleniumDriver: WebDriver, seleniumWindow: String) extends Container