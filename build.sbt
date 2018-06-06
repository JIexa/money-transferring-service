name := """money-transferring-service"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"
libraryDependencies += jdbc

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

libraryDependencies += "junit" % "junit" % "4.12" % Test
libraryDependencies += "org.mockito" % "mockito-core" % "2.18.3" % Test

libraryDependencies += "org.projectlombok" % "lombok" % "1.16.20" % "provided"

libraryDependencies += "io.ebean" % "ebean" % "11.15.4" % Test


// Make verbose tests
testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

