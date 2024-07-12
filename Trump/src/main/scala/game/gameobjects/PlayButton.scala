package game.gameobjects

import sge.core.*
import sge.swing.*
import game.StorageKeys

class PlayButton(text: String = "Play", position: Vector2D)
    extends GameButton(text, position):
  override def onButtonPressed: Engine => Unit = engine =>
    setWinCountersIfNotSet(engine)

    engine.loadScene(game.scenes.Game)

  private def setWinCountersIfNotSet(engine: Engine): Unit =
    Seq(StorageKeys.p1Wins, StorageKeys.p1Wins).foreach(key =>
      engine.storage.getOption[Int](key) match
        case Some(value) => ()
        case None        => engine.storage.set(key, 0)
    )

class PlaySmallDeckButton(
    text: String = "Play (small deck)",
    position: Vector2D
) extends PlayButton(text, position):
  override def onInit: Engine => Unit = engine =>
    engine.storage.unset(StorageKeys.useSmallDeck)
    super.onInit(engine)

  override def onButtonPressed: Engine => Unit = engine =>
    engine.storage.set(StorageKeys.useSmallDeck, true)
    super.onButtonPressed(engine)
