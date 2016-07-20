package com.github.cuzfrog.webdriver

object Elements {

  case class Element(attr: Map[String, String])
  case class Frame(override val attr: Map[String, String]) extends Element(attr) with WindowAlike

  sealed trait WindowAlike
  case class Window(head: String, title: String) extends WindowAlike
}