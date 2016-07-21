package com.github.cuzfrog.webdriver

import Elements._

case class Driver(_id: Long, name: String) {

  def getWindows: Seq[Window] = ???

  def getWindow:Window = ???

  /**
    * Kill driver on the server, and clean all associated elements in repository. Aka invoke WebDriver.quit().
    */
  def kill(): Unit = ???
  /**
    * Clean all associated elements in repository
    * @return number of elements cleaned.
    */
  def clean(): Long = ???
}