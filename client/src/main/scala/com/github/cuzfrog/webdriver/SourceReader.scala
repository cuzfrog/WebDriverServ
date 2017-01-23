package com.github.cuzfrog.webdriver

import java.io.InputStream

/**
  * Created by cuz on 1/23/17.
  */
private object SourceReader {
  /**
    * Given a resource path, read file on that path and return source code.
    *
    * @param name if ends with .scala, treat it as full path, otherwise transform it to:<br>
    *             "/parser/source/" + name + ".scala"
    * @return source code contained in the resource file.
    */
  def readSource(name: String): String = {
    val path = if (name.endsWith(".scala")) name else "/parser/source/" + name + ".scala"
    val stream: InputStream = getClass.getResourceAsStream(path)
    scala.io.Source.fromInputStream(stream).mkString
  }
}
