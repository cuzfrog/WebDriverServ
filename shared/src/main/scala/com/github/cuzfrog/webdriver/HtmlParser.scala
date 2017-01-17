package com.github.cuzfrog.webdriver

/**
  * Logic that is sent to server to control crawling processes.
  *
  * Created by cuz on 1/17/17.
  */
private[webdriver] trait HtmlParser {
  def parse(in: String): Any
}

/**
  * Pass input text through. Do nothing.
  */
private[webdriver] object NoParser extends HtmlParser{
  override def parse(in: String): String = in
}
