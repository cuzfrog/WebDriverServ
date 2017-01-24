package com.github.cuzfrog.webdriver

import com.typesafe.config.ConfigFactory

/**
  * Created by cuz on 1/24/17.
  */
private object ClientConfig {
  private val config = ConfigFactory.load().getConfig("webdriver.client")
  /**
    * Server uri with port. e.g. "localhost:60001"
    */
  val serverUri: String = config.getString("server-uri")
  /**
    * Server connection timeout in Seconds
    */
  val timeoutSec: Int = config.getInt("timeout")
  /**
    * Milliseconds
    */
  val actionInterval: Int = config.getInt("action-interval")

  val parserScriptDir: String = config.getString("parser-script-dir")
}
