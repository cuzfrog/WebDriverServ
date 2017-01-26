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
    Thread.sleep(3000)
    driver.kill()
  } finally {
    WebDriverClient.shutdownServer() //when necessary
    Thread.sleep(500)
    WebDriverClient.shutdownClient()
  }

}
