package com.github.cuzfrog.webdriver

import akka.actor.{Actor, ActorSystem, Props}

private[webdriver] object Server extends Logging {

  private val system = ActorSystem("WebDriverServ")
  private val handler = system.actorOf(Props[Handler], name = "handler")
  handler ! s"Server Initiation: Remoting now listens on addresses: [${system.name }@${ServConfig.host }:${ServConfig.port }]"

  private[webdriver] lazy val api = new ServerApi with ServerApiLogBefore with ServerApiLogAfter

  import system.dispatcher

  /**
    * Note: this method should not be called directly from Server, because
    * it only terminates actor system and does not close web drivers.
    * <br>
    * Call Server.shutDown instead if a completed shutting down is required.
    */
  private[webdriver] def terminateActorSystem(): Unit = {
    logger.info("Server is shutting down...")
    system.terminate().map { t =>
      logger.info(s"Actor system terminated: $t")
    }
  }

  /**
    * Shut down the server. Quit all web driver, clear caches and terminate actor system.
    * <br>Note: This method is for testing, and is only called from server side.
    */
  private[webdriver] def shutDown(): Unit = api.shutdown()
}

private[webdriver] class Handler extends Actor with Logging {
  def receive: Receive = {
    case r: Request =>
      logger.trace(s"Receive request: $r")
      val response: Response = try {
        r.execute(Server.api)
      } catch {
        case e: Exception =>
          logger.debug(s"Response Failed with exception:$e")
          if (ServConfig.printExceptionStackTraceEnabled) e.printStackTrace()
          Failed(e.getMessage, r)
      }
      sender ! response
    case s: String =>
      logger.info(s"[bounced]$s.")
      sender ! s"[bounced]$s."
    case other =>
      logger.info(s"Unknow msg received:[$other]")
    //sender ! other //send back
  }
}