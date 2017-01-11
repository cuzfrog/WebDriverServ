package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/11/17.
  */
object BounceTest extends App{
  WebDriverClient.bounceTest("some message345")
  WebDriverClient.shutdownClient()
}
