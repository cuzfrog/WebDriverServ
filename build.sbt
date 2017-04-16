import Settings._

/*
 * Version info is in file "version.sbt"
 * Common build info is in file "common.sbt"
 */

lazy val server = (project in file("./server")).dependsOn(shared % "test->test;compile->compile")
  .settings(commonSettings)
  .settings(Server.settings)
  .settings(
    name := "webdriver-server",
    libraryDependencies ++= Seq(
      "org.seleniumhq.selenium" % "selenium-java" % "2.53.1",
      "de.heikoseeberger" %% "akka-log4j" % "1.2.2",
      "org.scala-lang" % "scala-compiler" % "2.11.8",
      "org.apache.logging.log4j" % "log4j-core" % "2.7",
      "org.apache.logging.log4j" %% "log4j-api-scala" % "2.7",
      "org.apache.logging.log4j" % "log4j-api" % "2.7"
    )
  )

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
addCommandAlias("publishc", ";reload;client/publish-local;shared/publish-local;bumpVer")
addCommandAlias("publishBintray", ";reload;client/publish;shared/publish")
