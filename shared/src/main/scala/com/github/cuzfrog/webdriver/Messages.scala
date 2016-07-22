package com.github.cuzfrog.webdriver

import com.github.cuzfrog.webdriver.Elements._


/**
  * Interact with api, working as router.
  */
private[webdriver] object Messages {

  sealed trait Message

  sealed trait Request extends Message {
    def execute(api: Api): Response
  }
  case class NewDriver(name: String, typ: DriverTypes.DriverType) extends Request {
    def execute(api: Api) = ReadyDriver(api.newDriver(name, typ))
  }
  case class RetrieveDriver(name: String) extends Request {
    def execute(api: Api) = api.retrieveDriver(name) match {
      case Some(d) => ReadyDriver(d)
      case None => Failed(s"No such driver[$name] on server.")
    }
  }
  case class Kill(driver: Driver) extends Request {
    def execute(api: Api) = {
      val eleCnt = api.kill(driver)
      Success(s"Driver quit, $eleCnt elements cleaned.")
    }
  }
  case class Clean(driver: Driver) extends Request {
    def execute(api: Api) = {
      val eleCnt = api.clean(driver)
      Success(s"$eleCnt elements cleaned.")
    }
  }
  case class FindElement(parent_id: Long, attr: String, value: String) extends Request {
    def execute(api: Api) = ReadyElement(api.findElement(parent_id, attr, value))
  }
  case class FindElements(parent_id: Long, attr: String, value: String) extends Request {
    def execute(api: Api) = ReadyElements(api.findElements(parent_id, attr, value))
  }
  case class SendKeys(element: Element, keys: String) extends Request {
    def execute(api: Api) = {
      api.sendKeys(element, keys)
      Success("Keys sent.")
    }
  }
  case class Submit(element: Element) extends Request {
    def execute(api: Api) = {
      api.submit(element)
      Success("Submit")
    }
  }
  case class Click(element: Element) extends Request {
    def execute(api: Api) = {
      api.click(element)
      Success("Clicked")
    }
  }
  case class GetAttr(element: Element, attr: String) extends Request {
    def execute(api: Api) = Success(api.getAttr(element, attr))
  }
  case class GetText(element: Element) extends Request {
    def execute(api: Api) = Success(api.getText(element))
  }
  case class GetWindow(driver: Driver) extends Request {
    def execute(api: Api) = ReadyWindow(api.getWindow(driver))
  }
  case class GetWindows(driver: Driver) extends Request {
    def execute(api: Api) = ReadyWindows(api.getWindows(driver))
  }

  sealed trait Response extends Message
  case class Failed(msg: String) extends Response
  case class Success(msg: String) extends Response
  case class ReadyDriver(driver: Driver) extends Response
  case class ReadyWindow(window: Window) extends Response
  case class ReadyWindows(windows: Seq[Window]) extends Response
  case class ReadyElement(element: Element) extends Response
  case class ReadyElements(elements: Seq[Element]) extends Response

}