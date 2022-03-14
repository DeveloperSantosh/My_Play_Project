name := """my_project"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, ProtobufPlugin)
scalaVersion := "2.13.7"

libraryDependencies += guice

libraryDependencies ++= Seq(
  javaJdbc,
  "mysql" % "mysql-connector-java" % "8.0.28",
  "be.objectify" %% "deadbolt-java" % "2.8.1",
  // (optional) If you need scalapb/scalapb.proto or anything from google/protobuf/*.proto
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "io.grpc" % "grpc-netty" % "1.44.1",
  "io.grpc" % "grpc-stub" % "1.44.1",
  "io.grpc" % "grpc-auth" % "1.44.1",
)

Compile / PB.targets := Seq(
  PB.gens.java -> (Compile / sourceManaged).value ,
  scalapb.gen(javaConversions=true) -> (Compile / sourceManaged).value
)

////routesGenerator := InjectedRoutesGenerator
