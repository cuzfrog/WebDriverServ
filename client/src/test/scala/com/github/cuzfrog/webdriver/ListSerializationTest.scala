package com.github.cuzfrog.webdriver

import java.io.{ByteArrayInputStream, ByteArrayOutputStream, ObjectInputStream, ObjectOutputStream}

import akka.actor.ActorSystem
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

import scala.Serializable

/**
  * Created by Cause Frog on 7/30/2016.
  */
@deprecated("failed","0.1.8")
object ListSerializationTest extends App {

  val fun = (a: String) => a.contains("something")
  val fun1 = new Fun

  try {
    val response = WebDriverClient.Experimental.bounceTest(write(fun))
    print(response)
  } finally {
    Thread.sleep(3000)
    WebDriverClient.shutdownClient()
  }

  def write[A](obj: A): Array[Byte] = {
    val bo = new ByteArrayOutputStream()
    new ObjectOutputStream(bo).writeObject(obj)
    bo.toByteArray
  }


}
@SerialVersionUID(42L)
class Fun extends (String => Boolean) with Serializable {
  def apply(a: String): Boolean = a.contains("something")
}