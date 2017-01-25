package anywhere

import com.github.cuzfrog.webdriver.{Chrome, WebDriverClient}

import scala.language.implicitConversions

object WebDriverClientTest extends App {
  val driverName = "test1"
  try {
    val driver = WebDriverClient.retrieveDriver(driverName) match {
      case s@Some(_) => s
      case None => WebDriverClient.newDriver(driverName, Chrome)
    }

    implicit def getOption[T](option: Option[T]): T = option.get
    val window = driver.navigateTo("http://www.bing.com")
    window.findElement("id", "sb_form_q").sendKeys("Juno mission")
    window.findElement("id", "sb_form").submit()
    window.executeJS("console.log('testJS')")
    val jupiterCnt =
      window.findElement("id", "b_content").getInnerHtml("WordCountForJupiter").asInstanceOf[Option[Int]]

    println(s"There are $jupiterCnt 'jupiter' in the page content area.")
    scala.io.StdIn.readLine("press any to shut down the client.....")
  } finally {
    WebDriverClient.shutdownClient()
  }
  //WebDriverClient.shutdownServer(host) //when necessary
}
