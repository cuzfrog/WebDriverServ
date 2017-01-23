package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/23/17.
  */
object AppService extends App {
  Server //start server
  Thread.sleep(1000)
  scala.io.StdIn.readLine("Server is running...Press any key to exit...." + System.lineSeparator())
  Server.shutDown()
}
