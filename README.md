[![wercker status](https://app.wercker.com/status/762633d46c024b744891670f66d9339a/s "wercker status")](https://app.wercker.com/project/bykey/762633d46c024b744891670f66d9339a)

# WebDriver Sever

A server running Selenium WebDriver which is intended to reuse WebDriver instance.
When developing or debugging with WebDriver, we sometimes want to stay to the very page which can only be accessed by the driver who created it.
This is where this project comes in to hold driver instance in a standalone jvm.

If you are doing something alike, go [A list of headless browsers](http://www.asad.pw/HeadlessBrowsers/), see if you have a better choice.

Current under development.

###Feature:

1. This project is written in Scala and includes two parts:

   * A server that runs a Selenium WebDriver and accepts client instruction.

   * A client which controls the server and is put in your code as dependency.

2. Based on akka remoting. So client and driver can run on different "machine".

3. Add some convenient methods. (like auto switch to window or frame.)

4. Use typesafe Config. You can easily setup test environment.

5. Fine-tuned server logging and complete client document.

###How to Use:

#####Start server:

Right now, you need to build for yourself:

1. `git clone`

2. Go to [Selenium](http://www.seleniumhq.org/docs/03_webdriver.jsp#selenium-webdriver-s-drivers)
download associated driver(you can find some of them in Selenium's wiki page.).

3. goto server/application.conf  find and change:

    ```
    hostname = "your server ip"
    port = 60001
    ```
    ```
    webdriver {
      chrome.driver = "driver exe path"
      ie.driver = "driver exe path"
    }
    ```

4. enter sbt , `change` (or `re-start`, this command will trigger sbt-revolver to start the server)

#####Client code:

1. sbt `publishc`  (as you have cloned the whole project, this will publish client into your local repository.)

2. add client dependency to your project:

        "com.github.cuzfrog" %% "webdriver-client" % "some version"

3. code example:
(Try to retrieve stub of the driver on server. If failed create a new one.
Then navigate to www.bing.com and search "Juno mission")
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

#####How to shutdown server:

1. exit sbt  or  `re-stop`  will tell sbt-revolver to kill the jvm.
(Driver process will not be killed, shutdown hook not working.)

2. call client `shutdownServer()` (Recommended).

    _Quit single driver:_ call client `driver.kill()` this will not shutdown the server.

    _Clean server cache without quit driver:_ call client `driver.clean()`. This command remove all WebDriver instances from the cache
 associated with this `driver`.

#####Mechanism:

Client and server communication is based on Akka serialization of shared messages.
(Closure is not easy to send over the tunnel, which requires sending class definition.
Described blow.)
Client stubs of drivers, windows and elements are actually IDs, seen by the server.
On which a mutable Map is used as the repository to cache WebDriver instances.

#####About closure serialization and sending:
Failed experiments:
1. Trying to serialize closure itself. Remote needs class definition.
2. Deploy remote actor that encapsulates implementation. Remote needs actor definition.
3. Use macro to extract source/AST of function, send source to remote and compile at runtime.
Macro cannot resolve reference to source automatically.

__Ended up with solution:__
Define function as .scala source file in resource directory(or wherever reachable). Read it as pure source, and send
it to the remote server, then compiled.(REPL/scala script)

pros:
* Syntax check supported by IDE.
* No requirement of complete class definition, code snippet is valid.
* Support imports.(caveat: imports must be in the scope of the server.)



