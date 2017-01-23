import sbt.Keys._
import sbt._

object Settings {
  val commonSettings = Seq(
    organization := "com.github.cuzfrog",
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
    "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0"
  )
}