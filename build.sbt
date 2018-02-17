name := "scalachain"

version := "0.1"

scalaVersion := "2.12.4"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"

lazy val akkaVersion = "2.5.3"

libraryDependencies ++= Seq(
  "io.spray" %%  "spray-json" % "1.3.3",
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)


