package game.gameobjects

import sge.core.*
import game.behaviours.*
import game.*
import sge.core.behaviours.dimension2d.*

class Field(
    position: Vector2D = (0, 0),
    val spacing: Double = 0
) extends Behaviour
    with Positionable(position):

  val leftCard: CardImage = fieldCard(
    Versor2D.left * (Values.Dimensions.Cards.width / 2 + spacing)
  )

  override def onInit: Engine => Unit = engine =>
    engine.create(leftCard)
    super.onInit(engine)

  override def onUpdate: Engine => Unit = engine =>
    val placedCard = engine.gameModel.field.placedCards.headOption.map(_.card)
    if leftCard.card != placedCard then leftCard.card = placedCard
    super.onUpdate(engine)

  private def fieldCard(offset: Vector2D): CardImage =
    new Behaviour
      with CardImage(_card = Option.empty, _show = true)
      with Positionable
      with PositionFollower(this, positionOffset = offset)
      with ChangeableImageRenderer(
        width = Values.Dimensions.Cards.width,
        height = Values.Dimensions.Cards.height
      )
