[![wercker status](https://app.wercker.com/status/762633d46c024b744891670f66d9339a/s "wercker status")](https://app.wercker.com/project/bykey/762633d46c024b744891670f66d9339a)

# WebDriver Sever

A server running Selenium WebDriver which is intended to reuse WebDriver instance.
When developing or debugging with WebDriver, we sometimes want to stay to the very page which can only be accessed by the driver who created it.
This is where this project comes in to hold driver instance in a standalone jvm.

If you are doing something alike, go [A list of headless browsers](http://www.asad.pw/HeadlessBrowsers/), see if you have a better choice.
And an excellent web crawling library: [scala-scraper](https://github.com/ruippeixotog/scala-scraper)
 by ruippeixotog.

## Feature:

1. This project is written in Scala and includes two parts:
   * A server that runs a Selenium WebDriver and accepts client instruction.
   * A client which controls the server and is put in your code as dependency.
2. Based on akka remoting(Artery). 
3. Add some convenient methods to Selenium WebDriver. (like auto switch to window or frame.)
4. Use typesafe Config.
5. Complete client document.
6. Define html parsing script at client side, and execute at server side.
7. Supports JavaScript. Better browser emulation.

## How to Use:

### Start server:

Right now, you need to build for yourself:

1. `git clone `

2. Go to [Selenium](http://www.seleniumhq.org/docs/03_webdriver.jsp#selenium-webdriver-s-drivers)
download associated driver(you can find some of them in Selenium's wiki page.).

3. edit `server/src/test/resources/application.conf`:

    ```
    hostname = "your server ip or address"
    port = 60001 #or other number
    ```
    ```
    webdriver {
      chrome.driver = "driver executable path"
      ie.driver = "driver executable path"
    }
    ```

4. enter sbt , `test:run` or `run -Dconfig.file=your-config-file-path`(ignore settings in step 3)

### Client code:

1. sbt `publishc`  (as you have cloned the whole project, this will publish client into your local repository.)

2. add client dependency to your project:

        libraryDependencies += "com.github.cuzfrog" %% "webdriver-client" % "0.3.1"

3. code example:
(Try to retrieve stub of the driver on server. If failed create a new one.
Then navigate to www.bing.com and search "Juno mission" and count word "Jupiter")
    ```scala
    import com.github.cuzfrog.webdriver.{Chrome, WebDriverClient}
    import scala.language.implicitConversions
    
    object WebDriverClientTest extends App {
      val driverName = "test1"
      implicit def getOption[T](option: Option[T]): T = option.get
      
      try {
        val driver = WebDriverClient.retrieveOrNewDriver(driverName, Chrome)
         //when retrieving, all elements associated with this driver are purged on the server cache
        val window = driver.navigateTo("http://www.bing.com")
        window.findElement("id", "sb_form_q").sendKeys("Juno mission")
        window.findElement("id", "sb_form").submit()
        window.executeJS("console.log('testJS')")
        val jupiterCnt =
          window.findElement("id", "b_content").getInnerHtml("WordCountForJupiter").asInstanceOf[Option[Int]]
    
        println(s"There are $jupiterCnt 'jupiter' in the page content area.")
        Thread.sleep(3000)
        driver.kill() //when necessary
      } finally {
        WebDriverClient.shutdownServer() //when necessary
        Thread.sleep(500)
        WebDriverClient.shutdownClient()
      }
    }
    ```
    
    `WordCountForJupiter`.sc: (A script that is sent to be executed on server. See below.)
    
### Sending html parsing implementation to the server:

1. Define a String parsing function as script in .scala file under resources directory. e.g.
    ```scala
    //import whatever.package //need to define server side dependencies(Setp 2)
    (html: String) => {
          val Jupiter ="""\bjupiter\b""".r
          Jupiter.findAllIn(html.toLowerCase).toSeq.length
        }
    ```
in file `resources/scripts/WordCountForJupiter.sc` (default path, which can be changed via config.)

2. (Optional) add server side dependencies(two ways):
   * Modify `build.sbt` directly.(Not recommended.)
   * Provide a file named `server-extra-dependencies` under resources directory,
   and put in multi-lines dependency definitions as:`"net.ruippeixotog" %% "scala-scraper" % "1.2.0"`
        * Scala version will be parsed.
        * Dependency scope corresponds to resources scope, which means definitions in `test/resources` has `test` scope.

3. Use in client code:
    ```scala
    val element:ClientElement = ???
    element.getInnerHtml("WordCountForJupiter") //will return parsed result.
    ```

### How to shutdown server:

1. Exiting sbt will kill the jvm, but not the WebDriver process.(Not recommended)

2. Call client `shutdownServer()` (Recommended).

    _Quit single driver:_ call client `driver.kill()` this will not shutdown the server.

### Mechanism (Important):

Client and server communication is based on Akka serialization of shared messages.

Client stubs of drivers, windows and elements are actually IDs, seen by the server, 
on which mutable Maps is used as the repository to cache WebDriver/element instances. 
As time goes by, the cache may grow to a degree that clogs the GC.

So you may clean caches on the server in two ways:

1. Retrieve the driver. When retrieving, all elements associated with this driver will be purged
in the cache. The use case is pretty intuitive.
2. Explicitly call method:

        driver.clean()

In fact, Selenium doc states that elements may expire when window has refreshed. Thus 
useless references may pile on the server. Call `clean()` explicitly when needed.

### About closure serialization and sending:
Failed experiments:

1. Trying to serialize closure itself. Remote needs class definition.
2. Deploy remote actor that encapsulates implementation. Remote needs actor definition.
3. Use macro to extract source/AST of function, send source to remote and compile at runtime.
Macro cannot resolve references automatically.

__Ended up with solution:__
Define function as .scala source file in resource directory(or wherever reachable). Read it as pure source, and send
it to the remote server, then compiled.(REPL/scala script)



