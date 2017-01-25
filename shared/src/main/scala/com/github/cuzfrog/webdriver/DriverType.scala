package com.github.cuzfrog.webdriver

/**
  * Created by Cause Frog on 7/20/2016.
  */


sealed trait DriverType
case object IE extends DriverType
case object FireFox extends DriverType
case object Chrome extends DriverType
case object DriverType extends DriverType

