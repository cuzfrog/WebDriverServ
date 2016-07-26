package anywhere

import com.github.cuzfrog.webdriver.{DriverTypes, WebDriverClient}

object WebDriverClientTest extends App {
  val host = "192.168.56.101:60001"

  val driver = WebDriverClient.retrieveDriver(host, "test1") match {
    case s@Some(_) => s
    case None => WebDriverClient.newDriver(host, "test1", DriverTypes.Chrome)
  }

  driver
    .map(_.navigateTo("http://www.baidu.com"))


  //WebDriverClient.shutdownServer(host)
  scala.io.StdIn.readLine("press any to shut down the client.....")
  WebDriverClient.shutdownClient()

}
