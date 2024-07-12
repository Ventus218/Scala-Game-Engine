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
      _show = true
    )
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height,
      rotation = 90.degrees
    ):

  override def onStart: Engine => Unit = engine =>
    imagePath = Some(engine.gameModel.trumpCard.get.toImagePath)
    super.onStart(engine)

  override def onEarlyUpdate: Engine => Unit = engine =>
    if engine.gameModel.trumpCard == None then engine.destroy(this)
    super.onEarlyUpdate(engine)
