organization := "com.github.cuzfrog"
name := "WebDriverServ"
version := "0.0.1-p1"
scalaVersion := "2.11.8"

lazy val root = (project in file("."))

resolvers ++= Seq(
  "Local Maven Repository" at """file:///"""+Path.userHome.absolutePath+"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven"
)

libraryDependencies ++= Seq(
  "io.spray" %% "spray-routing" % "1.3.2",
  "io.spray" %% "spray-can" % "1.3.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.7"
)

mainClass in reStart := Some("com.github.cuzfrog.spatest.Server")

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.ManagedClasses
EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true