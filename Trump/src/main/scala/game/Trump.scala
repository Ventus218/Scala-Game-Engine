package game

import sge.core.*
import sge.swing.*

object Trump extends App:
  val swingIO = SwingIO
    .withTitle("Trump")
    .withSize(600, 400)
    .withPixelsPerUnitRatio(10)
    .build()
  val engine = Engine(swingIO, Storage(), 60)

  engine.run(scenes.Menu)
