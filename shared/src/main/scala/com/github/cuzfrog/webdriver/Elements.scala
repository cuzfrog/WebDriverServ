package com.github.cuzfrog.webdriver

object Elements {

  case class Element(id: Long)
  case class Frame(override val id: Long)
    extends Element(id) with WindowAlike

  sealed trait WindowAlike
  case class Window(id: Long, driver: Driver, head: String, title: String) extends WindowAlike
}