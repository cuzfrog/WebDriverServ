package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.DriverTypes.DriverType

/**
  * Shared Api.
  */
private[webdriver] trait Api {
  //client:
  def newDriver(name: String, typ: DriverType, waitSec: Int = 10): Driver
  def retrieveDriver(name: String): Option[Driver]
  def shutdown(): Unit

  //element and window:
  def findElement(webBody: WebBody, attr: String, value: String): Element
  def findElements(webBody: WebBody, attr: String, value: String): Seq[Element]
  def findElementEx(webBody: WebBody, attrPairs: List[(String, String)]): Element
  def checkElementExistence(webBody: WebBody, attr: String, value: String): Boolean
  def executeJS(webBody: WebBody, script: String): Any
  def sendKeys(element: Element, keys: String): Unit
  def clearText(element: Element): Unit
  def submit(element: Element): Unit
  def click(element: Element): Unit
  def getAttr(element: Element, attr: String): String
  def getText(element: Element): String
  def closeWindow(window: Window): Unit

  //driver:
  def kill(driver: Driver): Long
  def cleanCache(driver: Driver): Long
  def navigateTo(driver: Driver, url: String): Window
  def getWindow(driver: Driver): Window
  def getWindows(driver: Driver): Seq[Window]
}
