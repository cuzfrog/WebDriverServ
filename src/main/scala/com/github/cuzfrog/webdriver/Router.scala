package com.github.cuzfrog.webdriver

import akka.actor.Actor
import com.github.cuzfrog.webdriver.Elements.Element
import com.github.cuzfrog.webdriver.Messages._

private[webdriver] class Router extends Actor {

  def routeAndResponse(msg: Request): Response = try {
    msg match {
      case NewDriver(name, typ) => ReadyDriver(ServerApi.newDriver(name, typ))
      case RetrieveDriver(name) => ServerApi.retrieveDriver(name) match {
        case Some(d) => ReadyDriver(d)
        case None => Failed(s"No such driver[$name] on server.")
      }
      case Kill(driver) =>
        val eleCnt = ServerApi.kill(driver)
        Success(s"Driver quit, $eleCnt elements cleaned.")
      case Clean(driver) =>
        val eleCnt = ServerApi.clean(driver)
        Success(s"$eleCnt elements cleaned.")
      case FindElement(id, attr, value) => ReadyElement(ServerApi.findElement(id, attr, value))
      case SendKeys(element: Element, keys: String) => ServerApi.sendKeys(element, keys); Success("Keys sent.")
      case Submit(element) => ServerApi.submit(element); Success("Submit")
      case Click(element) => ServerApi.click(element); Success("Clicked")
      case GetAttr(element: Element, attr: String) => Success(ServerApi.getAttr(element, attr))
      case GetText(element) => Success(ServerApi.getText(element))
    }
  } catch {
    case e: Exception => Failed(e.getMessage)
  }

  def receive = {
    case m: Request => sender ! routeAndResponse(m)
  }

}
