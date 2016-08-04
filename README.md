# WebDriver Sever

A server running Selenium WebDriver which is intended to reuse WebDriver instance.
When developing or debugging with WebDriver, we sometimes want to stay to the very page which can only be accessed by the driver who created it.
This is where this project comes in to hold driver instance in a standalone jvm.

If you are doing something alike, go [A list of headless browsers](http://www.asad.pw/HeadlessBrowsers/), see if you have a better choice.

Current under development.

###Feature:

#####1.This project is written in Scala and includes two parts:

A server that runs a Selenium WebDriver and accepts client instruction.

A client which controls the server and is put in your code as dependency.

#####2.Based on akka remoting.

#####3.Add some convenient methods:

like auto switch to window or frame.

#####4.Use typesafe Config. You can easily setup test environment.

#####5.Fine-tuned server logging and complete client document.

###How to Use:

#####Start server:

Provide a application.conf at run path, as above. Go to Selenium download associated driver. Start the server.

Right now, you need to build for yourself:

1.git clone

2.sbt

3.change (or re-start, this command will trigger sbt-revolver to start the server)

#####Client code:

1.sbt publishc  (as you have cloned the whole project, this will publish client into your local repository.)

2.add client dependency to your project:

        "com.github.cuzfrog" %% "webdriver-client" % "some version"

3.code:
```scala

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
```


