package com.github.cuzfrog.webdriver

private[webdriver] sealed trait Element {val _id: Long ;val driver: Driver}
private[webdriver] case class Driver(_id: Long, name: String)
private[webdriver] case class CommonElement(_id: Long, driver: Driver) extends Element
private[webdriver] case class Frame(override val _id: Long, override val driver: Driver) extends Element
private[webdriver] case class Window(_id: Long, driver: Driver, handle: String, title: String)