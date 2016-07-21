package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.Elements.{Element, Frame, Window}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.{WebDriver, WebElement}

import scala.collection.mutable.ArrayBuffer

/**
  * Not thread safe, should be accessed within actor. Exceptions are handled in actor.
  */
private[webdriver] object ServerApi {

  import scala.collection.concurrent.TrieMap

  private val repository = TrieMap.empty[Long, Container]
  private val driverNameIndex = TrieMap.empty[String, Driver]
  private val idGen = new java.util.concurrent.atomic.AtomicLong
  private def newId: Long = idGen.getAndIncrement()

  import scala.language.implicitConversions

  private implicit def driverConversion(driver: Driver): WebDriver =
    getDriverContainer(driver).seleniumDriver
  private implicit def elementConversion(element: Element): WebElement =
    repository(element._id).asInstanceOf[ElementContainer].seleniumElement
  private implicit def windowConversion(window: Window): String =
    repository(window._id).asInstanceOf[WindowContainer].window.handle
  private def getDriverContainer(driver: Driver): DriverContainer = repository(driver._id).asInstanceOf[DriverContainer]

  def newDriver(name: String, typ: DriverTypes.DriverType): Driver = {
    val webDriver = typ match {
      case DriverTypes.IE => new InternetExplorerDriver()
      case DriverTypes.Chrome => new ChromeDriver()
      case DriverTypes.FireFox => new FirefoxDriver()
      case DriverTypes.HtmlUnit => new HtmlUnitDriver()
    }
    val driver = Driver(newId, name)
    repository.put(driver._id, DriverContainer(driver, webDriver))
    driverNameIndex.put(name, driver)
    driver
  }

  def retrieveDriver(name: String): Option[Driver] = driverNameIndex.get(name)

  import Elements.Element
  import org.openqa.selenium.By

  def findElement(id: Long, attr: String, value: String): Element = {
    val by = toBy(attr, value)
    val container = repository(id)
    val sEle = container match {
      case DriverContainer(_, webDriver) => webDriver.findElement(by)
      case ElementContainer(el, seleniumElement) => el match {
        case Frame(_, driver) => driver.switchTo().frame(seleniumElement).findElement(by)
        case _ => seleniumElement.findElement(by)
      }
      case WindowContainer(window, sd) => sd.switchTo().window(window.handle).findElement(by)
    }

    val element = sEle.getTagName.toLowerCase match {
      case "frame" | "iframe" => Frame(newId, container.driver)
      case _ => Element(newId, container.driver)
    }
    repository.put(element._id, ElementContainer(element, sEle))
    getDriverContainer(container.driver).elements += element._id
    element
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

  def sendKeys(element: Element, keys: String) = element.sendKeys(keys)
  def submit(element: Element) = element.submit()
  def click(element: Element) = element.click()
  def kill(driver: Driver): Long = {
    val dc = repository(driver._id).asInstanceOf[DriverContainer]
    dc.seleniumDriver.quit()
    driverNameIndex.remove(driver.name)
    repository.remove(driver._id)
    clean(dc.elements)
  }
  private def clean(elements: ArrayBuffer[Long]): Long = {
    elements.foreach(repository.remove)
    val cnt = elements.length
    elements.clear()
    cnt
  }
  def clean(driver: Driver) = {
    val dc = repository(driver._id).asInstanceOf[DriverContainer]
    clean(dc.elements)
  }
  def getAttr(element: Element, attr: String): String = element.getAttribute(attr)
  def getText(element: Element): String = element.getText

  def getWindow(driver: Driver): Window = {
    val dc = getDriverContainer(driver)
    val webDriver = dc.seleniumDriver
    val windowHandle = webDriver.getWindowHandle
    webDriver.switchTo().window(windowHandle)
    val window = Window(newId, driver, windowHandle, webDriver.getTitle)
    repository.put(window._id, WindowContainer(window, webDriver))
    dc.elements += window._id
    window
  }
  def getWindows(driver: Driver): Seq[Window] = ???

}
