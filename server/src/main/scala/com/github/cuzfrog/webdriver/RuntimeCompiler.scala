package com.github.cuzfrog.webdriver


import com.typesafe.scalalogging.LazyLogging

import scala.reflect.runtime.{universe => ru}
import scala.tools.reflect.ToolBox

/**
  * Created by cuz on 1/17/17.
  */
private object RuntimeCompiler extends LazyLogging {
  private val tb = ru.runtimeMirror(getClass.getClassLoader).mkToolBox()
  private def classDef(src: String) = {
    logger.debug("Parse logic class source for compilation:")
    println(src)
    tb.parse(src)
  }

  def compileLogic(src: String): Function[String, _] = {
    val instance = tb.eval(classDef(src))
    instance.asInstanceOf[Function1[String, _]]
  }

  //def getType[T: ru.TypeTag](instance: T): ru.Type = ru.typeOf[T]
}
