lazy val hello = taskKey[Unit]("An example task")

lazy val commonSettings = Seq(
  scalaVersion := "2.11.7",
  organization := "com.example"
)

lazy val openholidays = (project in file(".")).settings(
  version := "0.1",
  scalaVersion := "2.11.7",
  scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8"),
  libraryDependencies ++= {
    val akkaV = "2.3.9"
    val sprayV = "1.3.3"
    Seq(
      "io.spray" %% "spray-can" % sprayV,
      "io.spray" %% "spray-routing" % sprayV,
      "io.spray" %% "spray-json" % "1.3.2",
      "io.spray" %% "spray-testkit" % sprayV % "test",
      "com.typesafe.akka" %% "akka-actor" % akkaV,
      "com.typesafe.akka" %% "akka-testkit" % akkaV % "it,test",
      "joda-time" % "joda-time" % "2.7",
      "com.github.tototoshi" %% "slick-joda-mapper" % "2.1.0",
      "org.specs2" %% "specs2-core" % "2.3.11" % "it,test",
      "com.typesafe.slick" %% "slick" % "3.1.1",
      "com.h2database" % "h2" % "1.3.175",
      "org.slf4j" % "slf4j-nop" % "1.6.4"
    )
  },
  Revolver.settings,
  hello := {println("Hello!")}
).
settings(commonSettings: _*).
configs(IntegrationTest).
settings(Defaults.itSettings: _*)
