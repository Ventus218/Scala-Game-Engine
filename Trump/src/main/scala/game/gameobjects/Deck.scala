package game.gameobjects

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable
import game.Values
import game.StorageKeys
import model.Trump.*
import game.Utils.gameModel

class Deck(position: Vector2D)
    extends Behaviour
    with Positionable(position)
    with ImageRenderer(
      imagePath = Values.ImagePaths.cardBackside,
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height,
      priority = 10
    ):

  override def onEarlyUpdate: Engine => Unit = engine =>
    if engine.gameModel.deck.size <= 0 then engine.destroy(this)
    super.onEarlyUpdate(engine)
