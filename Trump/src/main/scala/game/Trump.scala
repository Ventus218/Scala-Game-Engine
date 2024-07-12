package game

import sge.core.*
import sge.swing.*
import java.awt.Color

object Trump extends App:
  val swingIO = SwingIO
    .withTitle("Trump")
    .withSize(1200, 800)
    .withPixelsPerUnitRatio(12)
    .withBackgroundColor(Color(60, 120, 80))
    .build()
  val engine = Engine(swingIO, Storage(), 60)

  engine.run(scenes.Menu)
