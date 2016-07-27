
organization := "com.github.cuzfrog"
name := "WebDriverServ"
scalaVersion := Settings.scalaVersion

resolvers ++= Seq(
  "Local Maven Repository" at """file:///""" + Path.userHome.absolutePath +"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "spray repo" at "http://repo.spray.io"
)


lazy val server = (project in file(".")).dependsOn(shared)
libraryDependencies ++= Seq(
  "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
  "com.typesafe.akka" %% "akka-actor" % "2.4.8",
  "com.typesafe.akka" %% "akka-remote" % "2.4.8",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "ch.qos.logback" % "logback-classic" % "1.1.3"
)
reColors := Seq("magenta")
mainClass in reStart := Some("com.github.cuzfrog.webdriver.Server")

lazy val client = (project in file("./client")).dependsOn(shared)
  .settings(
    organization := "com.github.cuzfrog",
    name := "WebDriverCli",
    scalaVersion := Settings.scalaVersion,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.8",
      "com.typesafe.akka" %% "akka-remote" % "2.4.8",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3"
    )
  )
  .disablePlugins(RevolverPlugin)

lazy val shared = (project in file("./shared"))
  .settings(
    organization := "com.github.cuzfrog",
    name := "WebDriverShared",
    scalaVersion := Settings.scalaVersion,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Seq(
    )
  ).disablePlugins(RevolverPlugin)

addCommandAlias("change",";re-stop;re-start")

//release:
import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  inquireVersions,
  setReleaseVersion,
  setNextVersion
)
addCommandAlias("bumpVer","release with-defaults")
