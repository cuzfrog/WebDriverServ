package com.github.cuzfrog.webdriver
/**
  * Created by cuz on 1/16/17.
  */
object RemoteActorTest extends App {

  import WebDriverClient.ExperimentalAndTest._

  val remoteActor = deployActorToServer[ActorToDeployRemotely]("test-remote-actor1")
  println("received reply:" + sendMessageTo(remoteActor, "Test34565"))

  WebDriverClient.shutdownClient()
}



