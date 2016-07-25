package com.github.cuzfrog.webdriver

import utest._

object WebDriverClientTest extends TestSuite {
  val tests = this {
    'test1 {
      val driver=WebDriverClient.newDriver("http://localhost:60001", "test1", DriverTypes.FireFox)
    }

  }


}
