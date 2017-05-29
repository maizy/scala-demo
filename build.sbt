name := "scala-demo"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.0.2",
  "org.clapper" %% "classutil" % "1.1.2"
)

scalacOptions ++= Seq(
  "-target:jvm-1.8",
  "-encoding", "UTF-8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-explaintypes",
  "-Xfatal-warnings",
  "-Xlint:_",
  "-Ywarn-dead-code",
  "-Ywarn-inaccessible",
  "-Ywarn-infer-any",
  "-Ywarn-nullary-override",
  "-Ywarn-nullary-unit",
  "-Ywarn-numeric-widen"
  // "-Ywarn-unused",
  // "-Ywarn-value-discard"
)


(test in Test) := {
  org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Test).toTask("").value
  org.scalastyle.sbt.ScalastylePlugin.scalastyle.in(Compile).toTask("").value
  (test in Test).value
}
scalastyleFailOnError := true
