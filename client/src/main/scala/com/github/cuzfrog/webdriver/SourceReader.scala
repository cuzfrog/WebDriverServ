package com.github.cuzfrog.webdriver

import java.io.InputStream

/**
  * Created by cuz on 1/23/17.
  */
private object SourceReader {
  def readSource(name: String): String = {
    val stream: InputStream = getClass.getResourceAsStream("/parser/source/" + name + ".scala")
    scala.io.Source.fromInputStream(stream).mkString
  }
}
