package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/17/17.
  */
object ParseLogicSendingTest extends App {

  import WebDriverClient.ExperimentalAndTest._

  val logic = (in: String) => {
    in + "987766"
  }

  println("received reply:" + sendParseLogic("""in + "123"  """))

  WebDriverClient.shutdownClient()
}
