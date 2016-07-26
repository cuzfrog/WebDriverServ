package anywhere

import com.github.cuzfrog.webdriver.{DriverTypes, WebDriverClient}

object WebDriverClientTest extends App {
  val host="localhost:60001"

  val driver = WebDriverClient.newDriver(host, "test1", DriverTypes.Chrome)
  driver.map(_.navigateTo("www.baidu.com"))


  //WebDriverClient.shutdownServer(host)
  scala.io.StdIn.readLine("press any to shut down the client.....")
  WebDriverClient.shutdownClient()

}
