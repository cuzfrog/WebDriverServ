package com.github.cuzfrog.webdriver

class WebDriver(val host: String, id: Long, var focus: Window) {

  def findElement(attr: String, value: String): Option[Element] = ???

  def getWindowsHandles: Seq[Window] = ???
}