name := "akka-quickstart-scala"

version := "1.0"

scalaVersion := "2.13.5"

lazy val akkaVersion = "2.6.13"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)
libraryDependencies += "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.0"

