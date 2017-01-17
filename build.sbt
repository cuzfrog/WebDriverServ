resolvers ++= Seq(
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "spray repo" at "http://repo.spray.io"
)
licenses += ("Apache-2.0", url("https://opensource.org/licenses/Apache-2.0"))
disablePlugins(RevolverPlugin)
shellPrompt in ThisBuild := { state => Project.extract(state).currentRef.project + "> " }
onLoad in Global := (onLoad in Global).value andThen (Command.process("project server", _))

lazy val server = (project in file("./server")).dependsOn(shared % "test->test;compile->compile")
  .settings(Settings.commonSettings)
  .settings(
    name := "webdriver-server",
    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.16",
      "org.scala-lang" % "scala-compiler" % "2.11.8",
      "ch.qos.logback" % "logback-classic" % "1.1.7"
    ) ++ Settings.commonDependencies,
    reColors := Seq("magenta"),
    mainClass in Test in reStart := Some("com.github.cuzfrog.webdriver.Server")
  )

lazy val client = (project in file("./client")).dependsOn(shared)
  .settings(Settings.commonSettings)
  .settings(
    name := "webdriver-client",
    libraryDependencies ++= Seq(
      "ch.qos.logback" % "logback-classic" % "1.1.7" % "provided"
    ) ++ Settings.commonDependencies
  )
  .disablePlugins(RevolverPlugin)

lazy val shared = (project in file("./shared"))
  .settings(Settings.commonSettings)
  .settings(
    name := "webdriver-shared",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.16" % "provided",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0" % "provided"
    )
  ).disablePlugins(RevolverPlugin)

addCommandAlias("change", ";re-stop;re-start")

//====================release======================:
import ReleaseTransformations._
import sbtrelease._

releaseProcess := Seq[ReleaseStep](
  inquireVersions,
  setReleaseVersion,
  setNextVersion
)
releaseNextVersion := { ver => Version(ver).map(_.bumpBugfix.string).getOrElse(versionFormatError) }
addCommandAlias("bumpVer", "release with-defaults")
addCommandAlias("publishc", ";reload;client/publish-local;shared/publish-local")
