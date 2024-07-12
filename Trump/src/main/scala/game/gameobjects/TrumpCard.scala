package game.gameobjects

import sge.core.*
import game.*
import game.behaviours.CardImage
import model.Cards.*
import sge.core.behaviours.dimension2d.Positionable

class TrumpCard(card: Card, position: Vector2D)
    extends Behaviour
    with Positionable(position)
    with CardImage(
      cardImagePath = card.toImagePath,
      show = true,
      rotation = 90.degrees
    ):

  override def onEarlyUpdate: Engine => Unit = engine =>
    if engine.gameModel.trumpCard == None then engine.destroy(this)
    super.onEarlyUpdate(engine)
