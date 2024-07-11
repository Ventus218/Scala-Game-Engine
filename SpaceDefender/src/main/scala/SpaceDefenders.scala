import sge.core.*
import sge.swing.*
import managers.*
import java.awt.Color

object SpaceDefenders extends App:
  println("Starting Space Defenders...")
  val engine: Engine = Engine(
    SwingIO
      .withTitle("Space Defenders")
      .withCenter(0, 0)
      .withSize(GameConstants.screenSize)
      .withPixelsPerUnitRatio(GameConstants.pixelsPerUnit)
      .withBackgroundColor(Color.black)
      .build(),
    Storage(),
    120
  )
  engine.run(SceneManager.menuScene)
  println("Thank you for playing!")
