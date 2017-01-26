resolvers ++= Seq(
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "spray repo" at "http://repo.spray.io"
)
licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))
shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }
onLoad in Global := (onLoad in Global).value andThen (Command.process("project server", _))
