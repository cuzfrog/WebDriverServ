package com.github.cuzfrog.webdriver

import java.io.InputStream
import java.util.Properties

import org.apache.logging.log4j.scala.Logging

/**
  * Created by cuz on 1/23/17.
  */
private object ServerApp extends App with Logging {
  val version = Option(getClass.getPackage.getImplementationVersion).getOrElse {
    val stream: InputStream = getClass.getResourceAsStream("/build-info.properties")
    val x = new Properties
    x.load(stream)
    x.getProperty("version")
  }


  if (args.contains("-t")) {
    try {
      Server //start server
      Thread.sleep(1000)
      scala.io.StdIn.readLine(s"--Test--Server[$version] is running...Press any key to exit...." + System.lineSeparator())
    } finally {
      Server.shutDown()
    }
  } else {
    logger.info(s"--Prod--Server[$version] is running in the background. Use client to shut it down.")
    Server
  }

}
