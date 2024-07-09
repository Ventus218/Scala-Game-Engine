import sge.core.*
import sge.swing.*
import managers.*
import java.awt.Color

object SpaceDefender extends App:
  println("Starting Space Defender...")
  val engine: Engine = Engine(
    SwingIO
      .withTitle("Space Defender")
      .withSize(GameManager.screenSize)
      .withPixelsPerUnitRatio(GameManager.pixelsPerUnit)
      .withBackgroundColor(Color.black)
      .build(),
    Storage(),
    50
  )
  engine.run(SceneManager.testScene)
  println("Thank you for playing!")
