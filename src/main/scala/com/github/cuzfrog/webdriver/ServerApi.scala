package com.github.cuzfrog.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver

/**
  * Created by scjf on 7/21/2016.
  */
object ServerApi {

  import scala.collection.concurrent.TrieMap

  private val repository = TrieMap.empty[Long, Container]
  private val driverNameIndex = TrieMap.empty[String, Driver]
  private val idGen = new java.util.concurrent.atomic.AtomicLong
  private def newId: Long = idGen.getAndIncrement()

  implicit def driverConversion(driver: Driver): WebDriver = repository(driver.id).asInstanceOf

  def newDriver(name: String, typ: DriverTypes.DriverType): Driver = {
    val webDriver = typ match {
      case DriverTypes.IE => new InternetExplorerDriver()
      case DriverTypes.Chrome => new ChromeDriver()
      case DriverTypes.FireFox => new FirefoxDriver()
      case DriverTypes.HtmlUnit => new HtmlUnitDriver()
    }
    val driver = Driver(newId, name)
    repository.put(driver.id, DriverContainer(webDriver))
    driverNameIndex.put(name, driver)
    driver
  }

  def retrieveDriver(name: String): Option[Driver] = driverNameIndex.get(name)

  import Elements.Element
  import org.openqa.selenium.{By, WebDriver}

  def findElement(id: Long, attr: String, value: String): Option[Element] = {
    val by = toBy(attr, value)
    try {
      val sEle = repository.get(id) match {
        case Some(DriverContainer(webDriver)) => webDriver.findElement(by)
        case Some(ElementContainer(seleniumElement)) => seleniumElement.findElement(by)
        case Some(WindowContainer(d, seleniumWindow)) => d.switchTo().window(seleniumWindow).findElement(by)
      }
      val e = Element(newId)
      repository.put(e.id, ElementContainer(sEle))
      Some(e)
    }
  }
  private def toBy(attr: String, value: String): By = attr.toLowerCase match {
    case "id" => By.id(value)
    case "name" => By.name(value)
    case "tag" | "tagname" => By.tagName(value)
    case "xpath" => By.xpath(value)
    case "class" | "classname" => By.className(value)
    case "css" | "cssselector" => By.cssSelector(value)
    case "link" | "linktext" => By.linkText(value)
    case "partiallink" | "partiallinktext" => By.partialLinkText(value)
  }
}
