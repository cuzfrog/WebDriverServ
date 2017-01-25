package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/25/17.
  */
object SerializationTest extends App {
  val request: Request = NewDriver("SomeName", Chrome, 5)
  WebDriverClient.ExperimentalAndTest.sendToServer(request)

  Thread.sleep(2000)
  WebDriverClient.shutdownClient()
}
