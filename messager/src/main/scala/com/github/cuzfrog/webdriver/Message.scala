package com.github.cuzfrog.webdriver

import spray.json._
import spray.json.DefaultJsonProtocol._
import com.typesafe.scalalogging.LazyLogging

sealed trait Message { val name = this.getClass.getSimpleName }

object Message extends LazyLogging {

  case class Raw(message: String, data: String)
  case class StartDriver(driver: String) extends Message
  case class FindElement(attr: String, value: String) extends Message
  case class SendKeys(uuid: String, keys: String) extends Message

  def unpickle(in: String): Option[Message] = try {
    import upickle.default._
    val raw = read[Raw](in)
    val msg = raw.message match {
      case "StartDriver" => read[StartDriver](raw.data)
      case "FindElement" => read[FindElement](raw.data)
      case "SendKeys"    => read[SendKeys](raw.data)
    }
    Some(msg)
  }

  def fromJson(in: String): Option[Message] = try {
    val msg = in.parseJson.convertTo[Map[String, String]]
    msg.get("message").map {
      case "StartDriver" => StartDriver(msg("driver"))
      case "FindElement" => FindElement(msg("attr"), msg("value"))
      case "SendKeys"    => SendKeys(msg("uuid"), msg("keys"))
    }
  } catch {
    case e: Exception =>
      e.printStackTrace()
      logger.debug(s"Bad message $in")
      None
  }
}