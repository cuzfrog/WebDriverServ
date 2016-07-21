package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.Elements.{Element, Frame, Window}
import org.openqa.selenium.{WebDriver, WebElement}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.ie.InternetExplorerDriver

import scala.languageFeature.implicitConversions

/**
  * Not thread safe, should be accessed within actor.
  */
object ServerApi {

  import scala.collection.concurrent.TrieMap

  private val repository = TrieMap.empty[Long, Container]
  private val driverNameIndex = TrieMap.empty[String, Driver]
  private val idGen = new java.util.concurrent.atomic.AtomicLong
  private def newId: Long = idGen.getAndIncrement()

  implicit def driverConversion(driver: Driver): WebDriver =
    repository(driver._id).asInstanceOf[DriverContainer].seleniumDriver
  implicit def elementConversion(element: Element): WebElement =
    repository(element._id).asInstanceOf[ElementContainer].seleniumElement
  implicit def windowConversion(window: Window): String =
    repository(window._id).asInstanceOf[WindowContainer].seleniumWindowHandle

  def newDriver(name: String, typ: DriverTypes.DriverType): Driver = {
    val webDriver = typ match {
      case DriverTypes.IE => new InternetExplorerDriver()
      case DriverTypes.Chrome => new ChromeDriver()
      case DriverTypes.FireFox => new FirefoxDriver()
      case DriverTypes.HtmlUnit => new HtmlUnitDriver()
    }
    val driver = Driver(newId, name)
    repository.put(driver._id, DriverContainer(webDriver))
    driverNameIndex.put(name, driver)
    driver
  }

  def retrieveDriver(name: String): Option[Driver] = driverNameIndex.get(name)

  import Elements.Element
  import org.openqa.selenium.By

  def findElement(id: Long, attr: String, value: String): Option[Element] = {
    val by = toBy(attr, value)
    try {
      repository.get(id).map { container =>
        val sEle = container match {
          case DriverContainer(_, webDriver) => webDriver.findElement(by)
          case ElementContainer(element, seleniumElement) => element match {
            case Frame(_, driver) => driver.switchTo().frame(seleniumElement).findElement(by)
            case _ => seleniumElement.findElement(by)
          }
          case WindowContainer(driver, sd, seleniumWindow) => sd.switchTo().window(seleniumWindow).findElement(by)
        }

        val e = sEle.getTagName.toLowerCase match {
          case "frame" | "iframe" => Frame(newId, container.driver)
          case _ => Element(newId,container.driver)
        }
        repository.put(e._id, ElementContainer(sEle))
      }
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
  private def clean(elements: Seq[Long]): Long = {
    elements.foreach(repository.remove)
    elements.length
  }
  def clean(driver: Driver) = {
    val dc = repository(driver._id).asInstanceOf[DriverContainer]
    clean(dc.elements)
  }
}
