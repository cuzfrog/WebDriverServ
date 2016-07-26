package com.github.cuzfrog.webdriver

private[webdriver] sealed trait Message

private[webdriver] sealed trait Request extends Message {
  def execute(api: Api): Response
}
private[webdriver] case class NewDriver(name: String, typ: DriverTypes.DriverType) extends Request {
  def execute(api: Api) = Ready[Driver](api.newDriver(name, typ))
}
private[webdriver] case class RetrieveDriver(name: String) extends Request {
  def execute(api: Api) = api.retrieveDriver(name) match {
    case Some(d) => Ready[Driver](d)
    case None => Failed(s"No such driver[$name] on server.")
  }
}
private[webdriver] case class Kill(driver: Driver) extends Request {
  def execute(api: Api) = {
    val eleCnt = api.kill(driver)
    Success(s"Driver quit, $eleCnt elements cleaned.")
  }
}
private[webdriver] case class Clean(driver: Driver) extends Request {
  def execute(api: Api) = {
    val eleCnt = api.clean(driver)
    Success(s"$eleCnt elements cleaned.")
  }
}
private[webdriver] case class Navigate(driver: Driver, url: String) extends Request {
  def execute(api: Api) = {
    val window = api.navigateTo(driver, url)
    Ready(window)
  }
}
private[webdriver] case class GetWindow(driver: Driver) extends Request {
  def execute(api: Api) = Ready[Window](api.getWindow(driver))
}
private[webdriver] case class GetWindows(driver: Driver) extends Request {
  def execute(api: Api) = Ready[Seq[Window]](api.getWindows(driver))
}
private[webdriver] case class FindElement(parent_id: Long, attr: String, value: String) extends Request {
  def execute(api: Api) = Ready[Element](api.findElement(parent_id, attr, value))
}
private[webdriver] case class FindElements(parent_id: Long, attr: String, value: String) extends Request {
  def execute(api: Api) = Ready[Seq[Element]](api.findElements(parent_id, attr, value))
}
private[webdriver] case class SendKeys(element: Element, keys: String) extends Request {
  def execute(api: Api) = {
    api.sendKeys(element, keys)
    Success("Keys sent.")
  }
}
private[webdriver] case class Submit(element: Element) extends Request {
  def execute(api: Api) = {
    api.submit(element)
    Success("Submit")
  }
}
private[webdriver] case class Click(element: Element) extends Request {
  def execute(api: Api) = {
    api.click(element)
    Success("Clicked")
  }
}
private[webdriver] case class GetAttr(element: Element, attr: String) extends Request {
  def execute(api: Api) = Success(api.getAttr(element, attr))
}
private[webdriver] case class GetText(element: Element) extends Request {
  def execute(api: Api) = Success(api.getText(element))
}
private[webdriver] case object Shutdown extends Request {
  def execute(api: Api) = {
    api.shutdown()
    Success("Server shutdown.")
  }
}

private[webdriver] sealed trait Response extends Message
private[webdriver] case class Failed(msg: String) extends Response
private[webdriver] case class Success(msg: String) extends Response
private[webdriver] case class Ready[T](data: T) extends Response

//  case class ReadyDriver(driver: Driver) extends Response
//  case class ReadyWindow(window: Window) extends Response
//  case class ReadyWindows(windows: Seq[Window]) extends Response
//  case class ReadyElement(element: Element) extends Response
//  case class ReadyElements(elements: Seq[Element]) extends Response

