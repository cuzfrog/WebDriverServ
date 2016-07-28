package anywhere

import com.github.cuzfrog.webdriver.{DriverTypes, WebDriverClient}

import scala.language.implicitConversions

object WebDriverClientTest extends App {
  val driverName = "test1"
  try {
    val driver = WebDriverClient.retrieveDriver(driverName) match {
      case s@Some(_) => s
      case None => WebDriverClient.newDriver(driverName, DriverTypes.Chrome)
    }

    implicit def getOption[T](option: Option[T]): T = option.get
    val window = driver.navigateTo("http://www.bing.com")
    window.findElement("id", "sb_form_q").sendKeys("Juno mission")
    window.findElement("id", "sb_form").submit()
    window.executeJS("console.log('testJS')")

    scala.io.StdIn.readLine("press any to shut down the client.....")
  } finally {
    WebDriverClient.shutdownClient()
  }
  //WebDriverClient.shutdownServer(host) //when necessary
}
