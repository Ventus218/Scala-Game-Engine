val scala3Version = "3.4.2"

lazy val sge = project
  .in(file("./ScalaGameEngine"))
  .settings(
    name := "ScalaGameEngine",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    assembly / assemblyOutputPath := file("./SGE.jar")
  )

lazy val exampleGame = project
  .in(file("./ExampleGame"))
  .dependsOn(sge)
  .settings(
    name := "ExampleGame",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    assembly / assemblyOutputPath := file("./ExampleGame.jar"),
    assembly / mainClass := Some("ExampleGame")
  )

lazy val trump = project
  .in(file("./Trump"))
  .dependsOn(sge)
  .settings(
    name := "Trump",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    assembly / assemblyOutputPath := file("./Trump.jar"),
    assembly / mainClass := Some("Trump")
  )
