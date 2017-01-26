package com.github.cuzfrog.webdriver

import scala.collection.mutable

/**
  * Created by cuz on 1/26/17.
  */
private object ServerStatus {
  val repository: mutable.Map[Long, Container] = mutable.Map.empty
  val driverNameIndex: mutable.Map[String, Driver] = mutable.Map.empty
  private val idGen = new java.util.concurrent.atomic.AtomicLong
  def newId: Long = idGen.getAndIncrement()

  val runningStatus = new java.util.concurrent.atomic.AtomicBoolean(true)
  def isRunning: Boolean = runningStatus.get()
}
