package com.github.cuzfrog.webdriver

/**
  * Shared Api.
  */
private[webdriver] trait Api {
  //client:
  def retrieveDriver(name: String, willCleanCache: Boolean): Option[Driver]
  def retrieveOrNewDriver(name: String, driverType: DriverType,
                          waitSec: Int, willCleanCache: Boolean): Driver
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
  def getInnerHtml(element: Element, parseLogic: String): Any

  //driver:
  def kill(driver: Driver): Long
  def cleanCache(driver: Driver): Long
  def navigateTo(driver: Driver, url: String): Window
  def getWindow(driver: Driver): Window
  def getWindows(driver: Driver): Seq[Window]
}
