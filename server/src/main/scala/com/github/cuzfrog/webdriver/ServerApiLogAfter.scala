package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.DriverTypes.DriverType
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by Cause Frog on 7/26/2016.
  */
private[webdriver] trait ServerApiLogAfter extends Api with LazyLogging {

  import System.lineSeparator

  abstract override def getWindows(driver: Driver): Seq[Window] = {
    val windows = super.getWindows(driver)
    logger.debug(s"[${driver.name}]getWindows:$lineSeparator${windows.mkString(lineSeparator)}")
    windows
  }
  abstract override def newDriver(name: String, typ: DriverType, waitSec: Int): Driver = {
    val driver = super.newDriver(name, typ)
    logger.debug(s"[${driver.name}]create new driver[$typ]")
    driver
  }
  abstract override def retrieveDriver(name: String): Option[Driver] = {
    val result = super.retrieveDriver(name)
    logger.debug(s"[$name]try to retrieve driver, result:$result")
    result
  }
  abstract override def findElement(webBody: WebBody, attr: String, value: String): Element = {
    val ele = super.findElement(webBody, attr, value)
    logger.debug(s"[${webBody.driver.name}]find element:${ele._id} by[attr=$attr,value=$value]")
    ele
  }
  abstract override def findElements(webBody: WebBody, attr: String, value: String): Seq[Element] = {
    val elements = super.findElements(webBody, attr, value)
    logger.debug(s"[${webBody.driver.name}]find elements:${elements.map(_._id)} by[attr=$attr,value=$value]")
    elements
  }
  abstract override def findElementEx(webBody: WebBody, attrPairs: List[(String, String)]): Element = {
    val element = super.findElementEx(webBody, attrPairs)
    logger.debug(s"[${webBody.driver.name}]find element:${element._id} by attr pairs:$attrPairs")
    element
  }
  abstract override def checkElementExistence(webBody: WebBody, attr: String, value: String): Boolean = {
    val result = super.checkElementExistence(webBody, attr, value)
    logger.debug(s"[${webBody.driver.name}]check element by[attr=$attr,value=$value],is existing?$result")
    result
  }
  abstract override def executeJS(webBody: WebBody, script: String): Any = {
    val result = super.executeJS(webBody, script)
    logger.debug(s"[${webBody.driver.name}] execute javascript:$lineSeparator $script")
    result
  }

  abstract override def sendKeys(element: Element, keys: String): Unit = {
    super.sendKeys(element, keys)
    logger.debug(s"[${element.driver.name}]send keys[$keys] to element:${element._id}")
  }
  abstract override def clearText(element: Element): Unit = {
    super.clearText(element)
    logger.debug(s"[${element.driver.name}]clear text of element:${element._id}")
  }
  abstract override def submit(element: Element): Unit = {
    super.submit(element)
    logger.debug(s"[${element.driver.name}]submit element:${element._id}")
  }
  abstract override def click(element: Element): Unit = {
    super.click(element)
    logger.debug(s"[${element.driver.name}]click element:${element._id}")
  }
  abstract override def kill(driver: Driver): Long = {
    val result = super.kill(driver)
    logger.debug(s"[${driver.name}]be killed, repository body cleaned:$result")
    result
  }
  abstract override def cleanCache(driver: Driver): Long = {
    val result = super.cleanCache(driver)
    logger.debug(s"[${driver.name}]be cleaned, repository body cleaned:$result")
    result
  }
  abstract override def getAttr(element: Element, attr: String): String = {
    val value = super.getAttr(element, attr)
    logger.debug(s"[${element.driver.name}]get value of attribute:$attr in element:${element._id}")
    value
  }
  abstract override def getText(element: Element): String = {
    val value = super.getText(element)
    logger.debug(s"[${element.driver.name}]get text")
    value
  }
  abstract override def closeWindow(window: Window): Unit = {
    super.closeWindow(window)
    logger.debug(s"[${window.driver.name}]close window:${window.title}")
  }
  abstract override def navigateTo(driver: Driver, url: String): Window = {
    val window = super.navigateTo(driver, url)
    logger.debug(s"[${driver.name}]navigate to $url and return window:${window.title}")
    window
  }
  abstract override def getWindow(driver: Driver): Window = {
    val window = super.getWindow(driver)
    logger.debug(s"[${driver.name}]get window:${window.title}")
    window
  }
  abstract override def shutdown(): Unit = {
    super.shutdown()
    logger.debug(s"Server shutdown..")
  }

}
