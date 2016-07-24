package com.github.cuzfrog.webdriver

/**
  * Created by scjf on 7/22/2016.
  */
object WebDriverClientTest extends App {
  val driver=WebDriverClient.newDriver("http://localhost:60001","test1",DriverTypes.FireFox)

}
