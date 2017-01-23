package anywhere

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

/**
  * Created by cuz on 1/23/17.
  */
object HtmlCleanerTest extends App {
  val text =
    """
      |<div class="highlight highlight-source-scala"><pre><span class="pl-k">val</span> <span class="pl-en">conflicts</span><span class="pl-k">:</span> <span class="pl-en">Set</span>[<span class="pl-en">Dependency</span>] <span class="pl-k">=</span> resolution.conflicts</pre></div>
    """.stripMargin

  val browser = JsoupBrowser()
  val doc=browser.parseString(text)
  val cont=doc >> "span"
  cont.foreach(println)
}
