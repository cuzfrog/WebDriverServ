package com.github.cuzfrog.webdriver

import Elements._

case class Driver(name: String) {

  def findElement(attr: String, value: String): Option[Element] = ???

  def getWindowsHandles: Seq[Window] = ???
}