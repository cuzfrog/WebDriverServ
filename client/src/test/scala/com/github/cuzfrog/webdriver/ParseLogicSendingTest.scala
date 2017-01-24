package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/17/17.
  */
object ParseLogicSendingTest extends App {

  import WebDriverClient.ExperimentalAndTest._

  val src = SourceReader.readSourceFromResources("Func1")

  println("received reply:" + sendParseLogic(src))

  WebDriverClient.shutdownClient()
}
