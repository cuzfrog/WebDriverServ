package com.github.cuzfrog.webdriver

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.typesafe.scalalogging.LazyLogging
import scala.tools.reflect.ToolBox
/**
  * Created by cuz on 1/16/17.
  */
object RemoteActorTest extends App {

  import WebDriverClient.Experimental._

  val remoteActor = deployActorToServer[ActorToDeployRemotely]("test-remote-actor1")
  println("received reply:" + sendMessgeTo(remoteActor, "Test34565"))

  WebDriverClient.shutdownClient()
}



