package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/13/17.
  */
object FunSendTest extends App {
  val reply = WebDriverClient.textParse("abd", _ + "123")
  println(reply)
  WebDriverClient.shutdownClient()
}
