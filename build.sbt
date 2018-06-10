val ScalatraVersion = "2.6.3"

organization := "com.ravi.teja"

name := "BitcoinExchange"

version := "0.1.0"

scalaVersion := "2.11.8"

resolvers += Classpaths.typesafeReleases

libraryDependencies ++= Seq(
  "org.scalatra" %% "scalatra" % ScalatraVersion,
  "org.scalatra" %% "scalatra-scalatest" % ScalatraVersion % "test",
  "ch.qos.logback" % "logback-classic" % "1.2.3" % "runtime",
  "org.eclipse.jetty" % "jetty-webapp" % "9.4.9.v20180320" % "container",
  "javax.servlet" % "javax.servlet-api" % "3.1.0" % "provided",
  "org.scalatra" %% "scalatra-json" % ScalatraVersion,
  "org.scalaj" % "scalaj-http_2.11" % "2.3.0",
  "net.liftweb" %% "lift-json" % "3.2.0",
  "org.json4s"   %% "json4s-jackson" % "3.5.2",
  "org.scalanlp" %% "breeze" % "0.8.1",
  "org.scalanlp" %% "breeze-natives" % "0.8.1"
)

enablePlugins(SbtTwirl)
enablePlugins(ScalatraPlugin)
