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
    logBuffered := false,
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % "2.4.17",
      "com.typesafe.akka" %% "akka-remote" % "2.4.17", {
        val aksVersion = if (scalaVersion.value.startsWith("2.12.")) "0.5.1" else "0.5.0"
        "com.github.romix.akka" %% "akka-kryo-serialization" % aksVersion
      }
    )
  )


  val publishSettings = Seq(
    publishTo := Some("My Bintray" at s"https://api.bintray.com/maven/cuzfrog/maven/${(name in ThisProject).value}/;publish=1")
  )
}