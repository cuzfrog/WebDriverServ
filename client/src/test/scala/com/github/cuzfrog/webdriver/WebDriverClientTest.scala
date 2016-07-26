package com.github.cuzfrog.webdriver

object WebDriverClientTest extends App {
  val host="localhost:60001"

  val driver = WebDriverClient.newDriver(host, "test1", DriverTypes.FireFox)

  WebDriverClient.shutdownServer(host)


  scala.io.StdIn.readLine("press any to shut down the client.....")
  WebDriverClient.shutdownClient()


}
