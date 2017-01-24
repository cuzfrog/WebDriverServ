package com.github.cuzfrog.webdriver

import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 1/24/17.
  */
private object ClientConfig {
  private val config = ConfigFactory.load()
  val arteryEnabled: Boolean = config.getBoolean("akka.remote.artery.enabled")

  val akkaProtocol:String = if (arteryEnabled) "" else ".tcp"
  private val cliCfg = config.getConfig("webdriver.client")
  /**
    * Server uri with port. e.g. "localhost:60001"
    */
  val serverUri: String = cliCfg.getString("server-uri")
  /**
    * Server connection timeout in Seconds
    */
  val timeoutSec: Int = cliCfg.getInt("timeout")
  /**
    * Milliseconds
    */
  val actionInterval: Int = cliCfg.getInt("action-interval")

  val parserScriptDir: String = cliCfg.getString("parser-script-dir")
}
