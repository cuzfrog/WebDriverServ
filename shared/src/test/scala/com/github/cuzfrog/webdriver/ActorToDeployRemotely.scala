package com.github.cuzfrog.webdriver

import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

/**
  * Created by cuz on 1/17/17.
  */
private[webdriver] class ActorToDeployRemotely extends Actor with LazyLogging {
  override def receive: Receive = {
    case s: String =>
      val result = s + "123"
      logger.debug(s"Received message now process it to:$result")
      sender ! result
  }
}