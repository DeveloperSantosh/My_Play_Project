// The Play plugin
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.8")


//addSbtPlugin("com.github.sbt" % "sbt-protobuf" % "0.7.1")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.3")
addSbtPlugin("com.github.sbt" % "sbt-protobuf" % "0.7.1")
libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.11.9"


// Defines scaffolding (found under .g8 folder)
// http://www.foundweekends.org/giter8/scaffolding.html
// sbt "g8Scaffold form"
addSbtPlugin("org.foundweekends.giter8" % "sbt-giter8-scaffold" % "0.13.1")
