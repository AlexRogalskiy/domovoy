name := "domovoy"

version := "0.1"

scalaVersion := "2.13.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "3.3.2",
  "org.xerial" % "sqlite-jdbc" % "3.28.0",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "org.jsoup" % "jsoup" % "1.12.1"
)
