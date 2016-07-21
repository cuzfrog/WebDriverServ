package com.github.cuzfrog.webdriver

object Elements {

  case class Element(_id: Long, driver: Driver)
  case class Frame(override val _id: Long,override val driver: Driver)
    extends Element(_id, driver) with WindowAlike

  sealed trait WindowAlike
  case class Window(_id: Long, driver: Driver, handle: String, title: String) extends WindowAlike
}