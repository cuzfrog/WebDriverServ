package com.github.cuzfrog.webdriver

import java.io.{FileNotFoundException, InputStream}

/**
  * Created by cuz on 1/23/17.
  */
private object SourceReader {
  /**
    * Given a resource path, read file on that path and return source code.<br>
    * if name is not specified, return empty String.
    *
    * @param name if ends with .scala, treat it as full path, otherwise transform it to:<br>
    *             "/scripts/" + name + ".sc" (path is configurable)
    * @return source code contained in the resource file.
    */
  def readSourceFromResources(name: String): String = if (name.nonEmpty) {
    val path = if (name.endsWith(".scala") || name.endsWith(".sc")) name else ClientConfig.parserScriptDir + "/" + name + ".sc"
    val stream: InputStream = getClass.getResourceAsStream(path)
    if (stream == null) throw new FileNotFoundException(s"Cannot find script file with name:$name")
    scala.io.Source.fromInputStream(stream).mkString
  } else ""
}
