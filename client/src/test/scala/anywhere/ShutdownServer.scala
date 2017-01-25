package anywhere

import com.github.cuzfrog.webdriver.WebDriverClient

/**
  * Created by cuz on 1/25/17.
  */
object ShutdownServer extends App{
  WebDriverClient.retrieveDriver("test1").foreach(_.kill())
  WebDriverClient.shutdownServer()

  Thread.sleep(2000)
  WebDriverClient.shutdownClient()
}
