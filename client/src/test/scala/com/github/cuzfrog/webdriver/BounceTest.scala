package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/11/17.
  */
object BounceTest extends App{
  val bounceMsg=WebDriverClient.ExperimentalAndTest.bounceTest("some message345")
  println(bounceMsg)
  WebDriverClient.shutdownClient()
}
