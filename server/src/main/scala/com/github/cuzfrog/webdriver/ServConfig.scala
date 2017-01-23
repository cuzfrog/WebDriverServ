package com.github.cuzfrog.webdriver

import com.typesafe.config.ConfigFactory
/**
  * Created by cuz on 1/12/17.
  */
private object ServConfig {
  private val config = ConfigFactory.load()
  val host: String = config.getString("akka.remote.artery.canonical.hostname")
  val port: Int = config.getInt("akka.remote.artery.canonical.port")

  private val wdrCfg = config.getConfig("webdriver")
  val chromeDriverPath: String = wdrCfg.getString("chrome.driver")
  val IEDriverPath: String = wdrCfg.getString("ie.driver")
}
