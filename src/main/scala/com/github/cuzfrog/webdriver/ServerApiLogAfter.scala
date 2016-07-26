package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.DriverTypes.DriverType
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Cause Frog on 7/26/2016.
  */
private[webdriver] trait ServerApiLogAfter extends Api with LazyLogging {

  import System.lineSeparator

  override def getWindows(driver: Driver): Seq[Window] = {
    val windows = super.getWindows(driver)
    logger.debug(s"[${driver.name}]getWindows:$lineSeparator${windows.mkString(lineSeparator)}")
    windows
  }
  override def newDriver(name: String, typ: DriverType): Driver = {
    val driver = super.newDriver(name, typ)
    logger.debug(s"[${driver.name}]create new driver[$typ]")
    driver
  }
  override def retrieveDriver(name: String): Option[Driver] = {
    val result = super.retrieveDriver(name)
    logger.debug(s"[$name]retrieve, result:$result")
    result
  }
  override def findElement(id: Long, attr: String, value: String): Element = {
    val ele = super.findElement(id, attr, value)
    logger.debug(s"[${ele.driver.name}]find element:${ele._id} by[attr=$attr,value=$value]")
    ele
  }
  override def findElements(id: Long, attr: String, value: String): Seq[Element] = {
    val elements = super.findElements(id, attr, value)
    logger.debug(s"[$id]find elements:${elements.map(_._id)} by[attr=$attr,value=$value]")
    elements
  }
  override def sendKeys(element: Element, keys: String): Unit = {
    super.sendKeys(element, keys)
    logger.debug(s"[${element.driver.name}]send keys[$keys] to element:${element._id}")
  }
  override def submit(element: Element): Unit = {
    super.submit(element)
    logger.debug(s"[${element.driver.name}]submit element:${element._id}")
  }
  override def click(element: Element): Unit = {
    super.click(element)
    logger.debug(s"[${element.driver.name}]click element:${element._id}")
  }
  override def kill(driver: Driver): Long = {
    val result = super.kill(driver)
    logger.debug(s"[${driver.name}]be killed, repository body cleaned:$result")
    result
  }
  override def clean(driver: Driver): Long =  {
    val result = super.clean(driver)
    logger.debug(s"[${driver.name}]be cleaned, repository body cleaned:$result")
    result
  }
  override def getAttr(element: Element, attr: String): String = {
    val value=super.getAttr(element,attr)
    logger.debug(s"[${element.driver.name}]get value of attribute:$attr in element:${element._id}")
    value
  }
  override def getText(element: Element): String = {
    val value=super.getText(element)
    logger.debug(s"[${element.driver.name}]get text")
    value
  }
  override def navigateTo(driver: Driver, url: String): Window = {
    val window=super.navigateTo(driver,url)
    logger.debug(s"[${driver.name}]navigate to $url and return window:$window")
    window
  }
  override def getWindow(driver: Driver): Window = {
    val window=super.getWindow(driver)
    logger.debug(s"[${driver.name}]get window:$window")
    window
  }
  override def shutdown(): Unit = {
    super.shutdown()
    logger.debug(s"Server shutdown..")
  }
}
