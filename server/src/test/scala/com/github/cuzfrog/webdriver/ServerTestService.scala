package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/17/17.
  */
object ServerTestService extends App {
  Server.main(Array.empty)
  Thread.sleep(1000)
  scala.io.StdIn.readLine("Server is running...Press any key to exit...." + System.lineSeparator())
  Server.shutDown()
}
