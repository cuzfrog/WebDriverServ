package com.github.cuzfrog.webdriver

/**
  * Created by cuz on 1/13/17.
  */
object FunSendTest extends App {

  val parser = new MyParser

  val reply = WebDriverClient
  println(reply)
  WebDriverClient.shutdownClient()
}

class MyParser extends Function[String,String]{
  override def apply(v1: String): String = v1 + "123"
}