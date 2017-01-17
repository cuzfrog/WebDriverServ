package com.github.cuzfrog.webdriver


import java.util.UUID

import com.typesafe.scalalogging.LazyLogging

import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox

/**
  * Created by cuz on 1/17/17.
  */
private[webdriver] object RuntimeCompiler extends LazyLogging{
  private val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()

  private def uuid = UUID.randomUUID()
  private def classDef(parseLogic: String, resultType: String = "String") = {
    val src =
      s"""
         |private class PL extends Function[String,$resultType]{
         |  override def apply(in:String):$resultType = $parseLogic
         |}
         |scala.reflect.classTag[PL].runtimeClass
    """.stripMargin
    logger.debug("Parse logic class source for compilation:")
    println(src)
    tb.parse(src)
  }

  def compileLogic(parseLogic: String): Function[String, _] = {
    tb.compile(classDef(parseLogic)).apply().asInstanceOf[Class[Function[String, _]]]
      .getConstructor().newInstance()
  }
}
