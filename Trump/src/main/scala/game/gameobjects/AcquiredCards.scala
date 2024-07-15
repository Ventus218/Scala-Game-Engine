package game.gameobjects

import sge.core.*
import sge.core.behaviours.dimension2d.*
import sge.swing.*
import game.behaviours.*
import game.*
import model.Cards.*
import java.awt.Color

class AcquiredCards(
    val player: String,
    position: Vector2D = (0, 0),
    rotation: Angle = 90.degrees
) extends Behaviour
    with Positionable(position)
    with CardImage(
      _card = Option.empty,
      _show = false
    )
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height,
      rotation = rotation
    ):

  override def onUpdate: Engine => Unit = engine =>
    val acquiredCards = engine.gameModel.player(player).acquiredCards
    val newCard = acquiredCards.headOption
    if card != newCard then card = newCard

    super.onUpdate(engine)
