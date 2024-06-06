val scala3Version = "3.4.2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "ScalaGameEngine",
    version := "0.1.0-SNAPSHOT",

    scalaVersion := scala3Version,

    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,

    assembly / assemblyOutputPath := file("./SGE.jar")
  )
