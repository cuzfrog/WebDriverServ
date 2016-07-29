resolvers ++= Seq(
  "Local Maven Repository" at """file:///""" + Path.userHome.absolutePath +"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "spray repo" at "http://repo.spray.io"
)


lazy val server = (project in file("./server")).dependsOn(shared)
  .settings(
    organization := "com.github.cuzfrog",
    name := "webdriver-server",
    scalaVersion := Settings.scalaVersion,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
      "com.typesafe.akka" %% "akka-actor" % "2.4.8",
      "com.typesafe.akka" %% "akka-remote" % "2.4.8",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.8",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" % "logback-classic" % "1.1.3"
    ),
    reColors := Seq("magenta"),
    mainClass in reStart := Some("com.github.cuzfrog.webdriver.Server")
  )

lazy val client = (project in file("./client")).dependsOn(shared)
  .settings(
    organization := "com.github.cuzfrog",
    name := "webdriver-client",
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
    name := "webdriver-shared",
    scalaVersion := Settings.scalaVersion,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Seq(
    )
  ).disablePlugins(RevolverPlugin)

addCommandAlias("change", ";re-stop;re-start")

//release:
import ReleaseTransformations._
import sbtrelease._

releaseProcess := Seq[ReleaseStep](
  inquireVersions,
  setReleaseVersion,
  setNextVersion
)
releaseNextVersion := { ver => Version(ver).map(_.bumpBugfix.string).getOrElse(versionFormatError) }
addCommandAlias("bumpVer", "release with-defaults")
addCommandAlias("publish-local-client", ";client/publish-local;shared/publish-local")
