package game.gameobjects

import sge.core.*
import game.behaviours.*
import game.*
import sge.core.behaviours.dimension2d.*

class Field(
    position: Vector2D = (0, 0)
) extends Behaviour
    with Positionable(position):

  val card: CardImage = new Behaviour
    with CardImage(_card = Option.empty, _show = true)
    with Positionable
    with PositionFollower(this)
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height
    )

  override def onInit: Engine => Unit = engine =>
    engine.create(card)
    super.onInit(engine)

  override def onUpdate: Engine => Unit = engine =>
    val placedCard = engine.gameModel.field.placedCards.headOption.map(_.card)
    if card.card != placedCard then card.card = placedCard
    super.onUpdate(engine)
