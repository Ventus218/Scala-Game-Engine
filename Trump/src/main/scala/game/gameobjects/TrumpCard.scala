package game.gameobjects

import sge.core.*
import game.*
import game.behaviours.CardImage
import model.Cards.*
import sge.core.behaviours.dimension2d.Positionable
import game.behaviours.ChangeableImageRenderer

class TrumpCard(position: Vector2D)
    extends Behaviour
    with Positionable(position)
    with CardImage(
      _card = Option.empty,
      _show = true
    )
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height,
      rotation = 90.degrees
    ):

  override def onStart: Engine => Unit = engine =>
    card = Some(engine.gameModel.trumpCard.get)
    super.onStart(engine)

  override def onEarlyUpdate: Engine => Unit = engine =>
    if engine.gameModel.trumpCard == None then engine.destroy(this)
    super.onEarlyUpdate(engine)
