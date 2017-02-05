package com.github.cuzfrog.webdriver

import java.security.MessageDigest

/**
  * Created by cuz on 2/5/17.
  */
private object MD5 {
  private val md: MessageDigest = MessageDigest.getInstance("MD5")
  def apply(inputStr: String): String = {
    md.digest(inputStr.getBytes()).map(0xFF & _).map { "%02x".format(_) }.foldLeft("") { _ + _ }
  }
}
