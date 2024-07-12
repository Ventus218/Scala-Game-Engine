package game.gameobjects

import sge.core.*
import sge.swing.*
import game.StorageKeys

class PlayButton(position: Vector2D) extends GameButton("Play", position):
  override def onButtonPressed: Engine => Unit = engine =>
    setWinCountersIfNotSet(engine)

    engine.loadScene(game.scenes.Game)

  private def setWinCountersIfNotSet(engine: Engine): Unit =
    Seq(StorageKeys.p1Wins, StorageKeys.p1Wins).foreach(key =>
      engine.storage.getOption[Int](key) match
        case Some(value) => ()
        case None        => engine.storage.set(key, 0)
    )
