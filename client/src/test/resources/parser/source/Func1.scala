

val ExtractorR ="""<[\d\w\s]+>(.*)<[\d\w\s/]+>""".r
(s: String) =>
  s match {
    case ExtractorR(contents) => contents
    case "1" => "one"
    case other => other + "(other)"
  }