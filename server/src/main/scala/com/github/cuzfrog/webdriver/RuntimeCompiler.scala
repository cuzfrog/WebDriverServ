package com.github.cuzfrog.webdriver

import scala.reflect.runtime.{universe => ru}
import scala.tools.reflect.ToolBox

/**
  * Created by cuz on 1/17/17.
  */
private object RuntimeCompiler extends Logging {
  private val tb = ru.runtimeMirror(getClass.getClassLoader).mkToolBox()
  private def classDef(src: String) = {
    logger.trace(s"Compile script.... md5 [${MD5(src)}]")
    tb.parse(src)
  }

  def compileLogic(src: String): Function[String, _] = try {
    val instance = tb.eval(classDef(src))
    instance.asInstanceOf[Function1[String, _]]
  } catch {
    case e: Throwable =>
      e.printStackTrace()
      throw ScalaReflectionException("Runtime compilation failed.")
  }

}
