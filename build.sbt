import Settings._

crossScalaVersions in ThisBuild := Seq("2.11.11", "2.12.2")
scalaVersion in ThisBuild := "2.12.2"

/*
 * Version info is in file "version.sbt"
 * Common build info is in file "common.sbt"
 */
val generateSh = taskKey[Unit]("Generate bat.")
lazy val server = (project in file("./server")).dependsOn(shared % "test->test;compile->compile")
  .settings(commonSettings)
  .settings(Server.settings)
  .settings(
    name := "webdriver-server",
    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
      "de.heikoseeberger" %% "akka-log4j" % "1.2.2",
      "org.scala-lang" % "scala-compiler" % scalaVersion.value,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.8.2",
      "org.apache.logging.log4j" % "log4j-core" % "2.8.2",
      "org.apache.logging.log4j" % "log4j-api" % "2.8.2"
    ),
    addCommandAlias("packServer", ";reload;clean;compile;universal:packageBin")
  ).enablePlugins(UniversalPlugin,JavaAppPackaging)

lazy val client = (project in file("./client")).dependsOn(shared)
  .settings(commonSettings, publishSettings, Client.readmeVersionSettings)
  .settings(
    name := "webdriver-client",
    libraryDependencies ++= Seq(
      "com.typesafe.scala-logging" %% "scala-logging" % "3.5.0",
      "net.ruippeixotog" %% "scala-scraper" % "1.2.0" % Test
    )
  )

lazy val shared = (project in file("./shared"))
  .settings(commonSettings, publishSettings)
  .settings(
    name := "webdriver-shared",
    libraryDependencies ++= Seq(
    )
  )

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
addCommandAlias("publishc", ";reload;+ client/publish-local;+ shared/publish-local")
addCommandAlias("publishBintray", ";reload;client/publish;shared/publish")
