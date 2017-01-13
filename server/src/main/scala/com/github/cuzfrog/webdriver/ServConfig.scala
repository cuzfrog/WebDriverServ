package com.github.cuzfrog.webdriver

import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 1/12/17.
  */
object ServConfig {
  private val config = ConfigFactory.load()

  val chromeDriverPath: String = config.getString("webdriver.chrome.driver")
  val IEDriverPath: String = config.getString("webdriver.ie.driver")
  val host: String = config.getString("akka.remote.netty.tcp.hostname")
  val port: Int = config.getInt("akka.remote.netty.tcp.port")
}
