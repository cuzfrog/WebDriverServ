package com.github.cuzfrog.webdriver

import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 1/12/17.
  */
private object ServConfig {
  private val config = ConfigFactory.load()
  val arteryEnabled: Boolean = config.getBoolean("akka.remote.artery.enabled")

  private val remoteConfigPath = if (arteryEnabled) "artery.canonical" else "netty.tcp"
  val host: String = config.getString(s"akka.remote.$remoteConfigPath.hostname")
  val port: Int = config.getInt(s"akka.remote.$remoteConfigPath.port")

  private val wdrCfg = config.getConfig("webdriver")
  val chromeDriverPath: String = wdrCfg.getString("chrome.driver")
  val IEDriverPath: String = wdrCfg.getString("ie.driver")
}
