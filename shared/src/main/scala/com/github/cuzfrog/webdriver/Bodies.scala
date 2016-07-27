package com.github.cuzfrog.webdriver



private[webdriver] case class Driver(_id: Long, name: String)
private[webdriver] sealed trait WebBody {val _id: Long ;val driver: Driver}
private[webdriver] sealed trait Element extends WebBody
private[webdriver] case class CommonElement(_id: Long, driver: Driver) extends Element
private[webdriver] sealed trait WindowLike {val _id: Long ;val driver: Driver}
private[webdriver] case class Frame(override val _id: Long, override val driver: Driver) extends Element with WindowLike
private[webdriver] case class Window(_id: Long, driver: Driver, handle: String, title: String) extends WindowLike with WebBody