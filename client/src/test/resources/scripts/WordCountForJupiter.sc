/**
  * This is a script counting "Jupiter" in a test html.
  * Created by cuz on 1/25/17.
  */

(html: String) => {
  val Jupiter ="""\bjupiter\b""".r
  Jupiter.findAllIn(html.toLowerCase).toSeq.length
}