name := """my_project"""
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)
scalaVersion := "2.12.1"

libraryDependencies += guice

// For implementing mysql database with jdbc in play
libraryDependencies ++= Seq(
  javaJdbc,
  "mysql" % "mysql-connector-java" % "8.0.28",
)

// For implementing role based security, permission and authentication in play
libraryDependencies += "be.objectify" %% "deadbolt-java" % "2.8.1"

// For enabling BCRYPT encryption in play
libraryDependencies += "org.mindrot" % "jbcrypt" % "0.4"

// https://mvnrepository.com/artifact/com.ticketfly/play-liquibase
libraryDependencies += "com.ticketfly" %% "play-liquibase" % "2.2"
//libraryDependencies +="org.liquibase"% "liquibase-core"% "4.9.0"

// For implementing protobuf in play
//libraryDependencies += "com.google.protobuf" % "protobuf-java" % "3.19.4" % "protobuf"
