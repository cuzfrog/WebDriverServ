organization := "com.github.cuzfrog"
name := "WebDriverServ"
version := Settings.version
scalaVersion := Settings.scalaVersion

resolvers ++= Seq(
  "Local Maven Repository" at """file:///"""+Path.userHome.absolutePath+"""\.m2\repository""",
  "bintray-cuzfrog-maven" at "http://dl.bintray.com/cuzfrog/maven",
  "spray repo" at "http://repo.spray.io"
)

lazy val server = (project in file(".")).dependsOn(client)
libraryDependencies ++= Seq(
  "io.spray" %% "spray-routing" % "1.3.2",
  "io.spray" %% "spray-can" % "1.3.2",
  "com.typesafe.akka" %% "akka-actor" % "2.4.7"
)
mainClass in reStart := Some("com.github.cuzfrog.spatest.Server")

lazy val client = (project in file("./client"))
  .settings(
    organization := "com.github.cuzfrog",
    name := "WebDriverCli",
    version := Settings.version,
    scalaVersion := Settings.scalaVersion,
    scalacOptions ++= Settings.scalacOptions,
    libraryDependencies ++= Seq(
      "io.spray" %%  "spray-json" % "1.3.2",
      "io.spray" %% "spray-can" % "1.3.2",
      "com.lihaoyi" %% "upickle" % "0.4.1",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "ch.qos.logback" %  "logback-classic" % "1.1.3"
    )
  )
  .disablePlugins(RevolverPlugin)

EclipseKeys.createSrc := EclipseCreateSrc.Default + EclipseCreateSrc.ManagedClasses
EclipseKeys.withSource := true
EclipseKeys.withJavadoc := true