package com.github.cuzfrog.webdriver
/**
  * Created by cuz on 1/25/17.
  */
private trait ServerApiLogBefore extends ServerApi with Logging {
  abstract override def newDriver(name: String, typ: DriverType, waitSec: Int): Driver = {
    val driverPath = System.getProperty("webdriver.chrome.driver")
    logger.trace(s"Begin to create driver[$typ], path in property is[$driverPath] ")
    super.newDriver(name, typ, waitSec)
  }

  abstract override def kill(driver: Driver): Long = {
    logger.trace(s"Try to kill $driver")
    super.kill(driver)
  }
}
