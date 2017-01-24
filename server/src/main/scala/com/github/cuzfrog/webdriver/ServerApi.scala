package com.github.cuzfrog.webdriver

import java.util.concurrent.TimeUnit

import com.github.cuzfrog.webdriver.DriverTypes.DriverType
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.ie.InternetExplorerDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.support.ui.{ExpectedConditions, WebDriverWait}
import org.openqa.selenium.{JavascriptExecutor, WebDriver, WebElement}

import scala.collection.JavaConversions._
import scala.collection.mutable.ArrayBuffer

/**
  * Not thread safe, should be accessed within actor. Exceptions are handled in actor.
  */
private[webdriver] class ServerApi extends Api {

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
  private implicit def driverToJSexecutioner(driver: Driver): JavascriptExecutor = driverConversion(driver).asInstanceOf[JavascriptExecutor]
  private def getDriverContainer(driver: Driver): DriverContainer = repository(driver._id).asInstanceOf[DriverContainer]

  import org.openqa.selenium.By

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

  override def newDriver(name: String, typ: DriverType, waitSec: Int): Driver = {
    val webDriver = typ match {
      case DriverTypes.IE =>
        val capabilities = DesiredCapabilities.internetExplorer()
        capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true)
        new InternetExplorerDriver(capabilities)
      case DriverTypes.Chrome => new ChromeDriver()
      case DriverTypes.FireFox => throw new UnsupportedOperationException("Problematic, not implemented yet.") //new FirefoxDriver()
      case DriverTypes.HtmlUnit => throw new UnsupportedOperationException("Problematic,  not implemented yet.") //new HtmlUnitDriver()
    }
    webDriver.manage().timeouts().implicitlyWait(waitSec, TimeUnit.SECONDS)
    //implicit waiting
    val driver = Driver(newId, name)
    repository.put(driver._id, DriverContainer(driver, webDriver))
    driverNameIndex.put(name, driver)
    driver
  }

  override def retrieveDriver(name: String): Option[Driver] = driverNameIndex.get(name)

  override def findElement(webBody: WebBody, attr: String, value: String): Element = {
    helperFindElements(webBody, attr, value) match {
      case Nil => throw new NoSuchElementException(s"[${webBody.driver.name }]Cannot find element with:attr:$attr ,value:$value")
      case seq => seq.head
    }
  }

  override def findElements(webBody: WebBody, attr: String, value: String): Seq[Element] = {
    helperFindElements(webBody, attr, value)
  }
  override def findElementEx(webBody: WebBody, attrPairs: List[(String, String)]): Element = {
    require(attrPairs.nonEmpty, s"[${webBody.driver.name }]find element failed, because there is no attr pairs set.")
    val (firstAttr, firstValue) = attrPairs.head
    val elementsFromDriver = helperFindElements(webBody, firstAttr, firstValue)
    val pairs = attrPairs.map { e => (e._1, e._2) }
    def recursivelyFilter(atPr: List[(String, String)], elesToFilter: Seq[Element]): Element = {
      if (elesToFilter.isEmpty)
        throw new NoSuchElementException(s"[${webBody.driver.name }]Cannot find element with:$pairs")
      if (atPr.isEmpty) elesToFilter.head
      else {
        val (at, v) = atPr.head
        val elesLeft = elesToFilter.filter { e => e.getAttribute(at) == v } //apply custom interaction function
        recursivelyFilter(atPr.tail, elesLeft)
      }
    }
    recursivelyFilter(attrPairs.tail, elementsFromDriver)
  }
  private def helperFindElements(webBody: WebBody, attr: String, value: String): Seq[Element] = {
    val by = toBy(attr, value)
    val container = repository(webBody._id)
    val sEles = container match {
      case DriverContainer(_, webDriver) => webDriver.findElements(by)
      case ElementContainer(el, seleniumElement) => el match {
        case Frame(_, driver) => driver.switchTo().frame(seleniumElement).findElements(by)
        case _ => seleniumElement.findElements(by)
      }
      case WindowContainer(window, sd) => sd.switchTo().window(window.handle).findElements(by)
    }
    val elements = sEles.map {
      sEle =>
        val ele = sEle.getTagName.toLowerCase match {
          case "frame" | "iframe" => Frame(newId, container.driver)
          case _ => CommonElement(newId, container.driver)
        }
        repository.put(ele._id, ElementContainer(ele, sEle))
        getDriverContainer(container.driver).elements += ele._id
        ele
    }
    elements.toList
  }

  override def checkElementExistence(webBody: WebBody, attr: String, value: String): Boolean = try {
    val by = toBy(attr, value)
    new WebDriverWait(webBody.driver, 0).until(ExpectedConditions.presenceOfElementLocated(by))
    true
  } catch {
    case e: org.openqa.selenium.TimeoutException => false
  }
  override def executeJS(webBody: WebBody, script: String): Any = {
    webBody.driver.executeScript(script)
  }

  override def sendKeys(element: Element, keys: String): Unit = element.sendKeys(keys)
  override def clearText(element: Element): Unit = element.clear()
  override def submit(element: Element): Unit = element.submit()
  override def click(element: Element): Unit = element.click()
  override def kill(driver: Driver): Long = {
    val dc = repository(driver._id).asInstanceOf[DriverContainer]
    dc.seleniumDriver.quit()
    driverNameIndex.remove(driver.name)
    repository.remove(driver._id)
    cleanCache(dc.elements)
  }
  private def cleanCache(elements: ArrayBuffer[Long]): Long = {
    elements.foreach(repository.remove)
    val cnt = elements.length
    elements.clear()
    cnt
  }
  override def cleanCache(driver: Driver): Long = {
    val dc = repository(driver._id).asInstanceOf[DriverContainer]
    cleanCache(dc.elements)
  }
  override def getAttr(element: Element, attr: String): String = element.getAttribute(attr)

  override def getText(element: Element): String = element.getText

  override def getInnerHtml(element: Element, parseScript: String): Any = if (parseScript.nonEmpty) {
    val parser = RuntimeCompiler.compileLogic(parseScript)
    parser.apply(element.getAttribute("innerHTML"))
  } else {
    element.getAttribute("innerHTML")
  }

  override def closeWindow(window: Window): Unit = window.driver.close()

  override def navigateTo(driver: Driver, url: String): Window = {
    driver.get(url)
    getWindow(driver)
  }

  override def getWindow(driver: Driver): Window = {
    val dc = getDriverContainer(driver)
    val webDriver = dc.seleniumDriver
    val windowHandle = webDriver.getWindowHandle
    createRegisterWindow(dc, driver, webDriver, windowHandle)
  }
  override def getWindows(driver: Driver): Seq[Window] = {
    val dc = getDriverContainer(driver)
    val webDriver = dc.seleniumDriver
    val windowHandles = webDriver.getWindowHandles
    windowHandles.map {
      windowHandle =>
        createRegisterWindow(dc, driver, webDriver, windowHandle)
    }.toList
  }
  private def createRegisterWindow(dc: DriverContainer, driver: Driver, webDriver: WebDriver, windowHandle: String) = {
    webDriver.switchTo().window(windowHandle)
    val window = Window(newId, driver, windowHandle, webDriver.getTitle)
    repository.put(window._id, WindowContainer(window, webDriver))
    dc.elements += window._id
    window
  }

  override def shutdown(): Unit = {
    repository.clear()
    driverNameIndex.foreach {
      _._2.quit()
    }
    Server.terminateActorSystem()
  }

}
