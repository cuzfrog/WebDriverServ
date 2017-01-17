package com.github.cuzfrog.webdriver

import scala.reflect.runtime.universe
import scala.tools.reflect.ToolBox
/**
  * Created by cuz on 1/13/17.
  */
private object RuntimeCompilationTest{
  val tb = universe.runtimeMirror(getClass.getClassLoader).mkToolBox()

  val classDef = tb.parse {
    """
      |private class MyParser extends Function[String,String]{
      |  override def apply(v1: String): String = v1 + "123"
      |}
      |
      |scala.reflect.classTag[MyParser].runtimeClass
    """.stripMargin
  }


  val clazz = tb.compile(classDef).apply().asInstanceOf[Class[Function[String,String]]]

  val instance = clazz.getConstructor().newInstance()
  println(instance.apply("asdf"))
}

