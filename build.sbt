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

lazy val spaceDefender = project
  .in(file("./SpaceDefender"))
  .dependsOn(sge)
  .settings(
    name := "SpaceDefender",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    assembly / assemblyOutputPath := file("./SpaceDefender.jar"),
    assembly / mainClass := Some("SpaceDefender")
  )

lazy val stealthGame = project
  .in(file("./StealthGame"))
  .dependsOn(sge)
  .settings(
    name := "StealthGame",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % Test,
    assembly / assemblyOutputPath := file("./StealthGame.jar")
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
