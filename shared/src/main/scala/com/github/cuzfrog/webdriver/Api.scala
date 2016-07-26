package com.github.cuzfrog.webdriver

/**
  * Shared Api.
  */
private[webdriver] trait Api {
  def newDriver(name: String, typ: DriverTypes.DriverType): Driver
  def retrieveDriver(name: String): Option[Driver]
  def findElement(id: Long, attr: String, value: String): Element
  def findElements(id: Long, attr: String, value: String): Seq[Element]
  def sendKeys(element: Element, keys: String): Unit
  def submit(element: Element): Unit
  def click(element: Element): Unit
  def kill(driver: Driver): Long
  def clean(driver: Driver): Long
  def getAttr(element: Element, attr: String): String
  def getText(element: Element): String
  def navigateTo(driver: Driver, url: String): Window
  def getWindow(driver: Driver): Window
  def getWindows(driver: Driver): Seq[Window]
  def shutdown(): Unit
}
