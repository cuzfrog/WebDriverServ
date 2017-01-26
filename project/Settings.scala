import sbt.Keys._
import sbt._

object Settings {
  val commonSettings = Seq(
    organization := "com.github.cuzfrog",
    crossScalaVersions := Seq("2.11.8", "2.12.1"),
    scalaVersion := "2.11.8",
    scalacOptions ++= Seq(
      "-Xlint",
      "-unchecked",
      "-deprecation",
      "-feature"),
    logBuffered := false
  )

  val commonDependencies = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.16",
    "com.typesafe.akka" %% "akka-remote" % "2.4.16",
    "com.github.romix.akka" %% "akka-kryo-serialization" % "0.5.0",
    "org.apache.logging.log4j" %% "log4j-api-scala" % "2.7",
    "org.apache.logging.log4j" % "log4j-api" % "2.7"
  )

  val publishSettings = Seq(
    publishTo := Some("My Bintray" at s"https://api.bintray.com/maven/cuzfrog/maven/${(name in ThisProject).value }/;publish=1")
  )
}