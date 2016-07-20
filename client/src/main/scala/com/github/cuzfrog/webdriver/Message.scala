package com.github.cuzfrog.webdriver

import com.typesafe.scalalogging.LazyLogging
import java.nio.ByteBuffer

sealed trait Message { val name = this.getClass.getSimpleName }

object Message extends LazyLogging {

  case class Raw(message: String, data: ByteBuffer) extends Message
  case class StartDriver(driver: String) extends Message
  case class FindElement(attr: String, value: String) extends Message
  case class SendKeys(uuid: String, keys: String) extends Message
  
  import boopickle.Default._
  val pickler = compositePickler[Message]

  def unpickle(in: ByteBuffer): Option[Message] = try {
    val raw = Unpickle[Raw].fromBytes(in)
    val msg = raw.message match {
      case "StartDriver" => Unpickle[StartDriver].fromBytes(raw.data)
      case "FindElement" => Unpickle[FindElement].fromBytes(raw.data)
      case "SendKeys"    => Unpickle[SendKeys].fromBytes(raw.data)
    }
    Some(msg)
  } catch {
    case e: Exception =>
      e.printStackTrace()
      logger.debug(s"Bad message $in")
      None
  }

}