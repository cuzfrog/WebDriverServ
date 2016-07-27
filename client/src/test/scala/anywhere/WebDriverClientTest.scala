package anywhere

import com.github.cuzfrog.webdriver.{DriverTypes, WebDriverClient}

import scala.language.implicitConversions

object WebDriverClientTest extends App {
  val host = "192.168.56.101:60001"
  val driverName="test1"
  val driver = WebDriverClient.retrieveDriver(host, driverName) match {
    case s@Some(_) => s
    case None => WebDriverClient.newDriver(host, driverName, DriverTypes.Chrome)
  }

  implicit def getOption[T](option: Option[T]): T = option.get
  val window=driver.navigateTo("http://www.bing.com")
  window.findElement("id", "sb_form_q").sendKeys("Juno mission")
  window.findElement("id","sb_form").submit()
  window.executeJS("alert('testJS')")

  scala.io.StdIn.readLine("press any to shut down the client.....")
  WebDriverClient.shutdownClient()
  //WebDriverClient.shutdownServer(host) //when necessary
}
