package com.github.cuzfrog.webdriver

/**
  * Created by scjf on 7/22/2016.
  */
case class Frame(override val _id: Long,override val driver: Driver)
  extends Element(_id, driver)