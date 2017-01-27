/**
  * This is a test script.
  */

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.dsl.DSL._
val text =
  """
    |<div class="highlight highlight-source-scala"><pre><span class="pl-k">val</span> <span class="pl-en">conflicts</span><span class="pl-k">:</span> <span class="pl-en">Set</span>[<span class="pl-en">Dependency</span>] <span class="pl-k">=</span> resolution.conflicts</pre></div>
  """.stripMargin

val browser = JsoupBrowser()
val doc=browser.parseString(text)
val cont=doc >> "span"
cont.foreach(println)

val ExtractorR ="""<[\d\w\s]+>(.*)<[\d\w\s/]+>""".r
(s: String) =>
  s match {
    case ExtractorR(contents) => contents
    case "1" => "one"
    case other => other + "(other)"
  }