name := """my_project"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.13.7"

libraryDependencies += guice

libraryDependencies ++= Seq(
  javaJdbc,
  "mysql" % "mysql-connector-java" % "8.0.28",
  "be.objectify" %% "deadbolt-java" % "2.8.1"
)

//routesGenerator := InjectedRoutesGenerator
