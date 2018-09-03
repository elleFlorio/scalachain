name := "scalachain"

version := "0.1"

scalaVersion := "2.12.4"

resolvers ++= Seq(
  "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/",
  Resolver.jcenterRepo
)

lazy val akkaVersion = "2.5.3"
lazy val akkaHttpVersion = "10.1.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-testkit" % "2.5.14" % Test,
  "org.scalatest" %% "scalatest" % "3.0.4" % "test",
  "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
  "org.iq80.leveldb" % "leveldb" % "0.10",
  "org.fusesource.leveldbjni" % "leveldbjni-all" % "1.8",
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.github.dnvriend" %% "akka-persistence-inmemory" % "2.5.1.1"
)


