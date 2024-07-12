package game.gameobjects

import sge.core.*
import game.behaviours.*
import game.*
import sge.core.behaviours.dimension2d.*

class Hand(
    val player: String,
    position: Vector2D = (0, 0),
    val spacing: Double = 0,
    val show: Boolean
) extends Behaviour
    with Positionable(position):

  val leftCard: CardImage = new Behaviour
    with CardImage(_card = Option.empty, _show = show)
    with Positionable
    with PositionFollower(
      this,
      positionOffset = Versor2D.left * (Values.Dimensions.Cards.width + spacing)
    )
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height
    )

  val centerCard: CardImage = new Behaviour
    with CardImage(_card = Option.empty, _show = show)
    with Positionable
    with PositionFollower(this)
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height
    )

  val rightCard: CardImage = new Behaviour
    with CardImage(_card = Option.empty, _show = show)
    with Positionable
    with PositionFollower(
      this,
      positionOffset =
        Versor2D.right * (Values.Dimensions.Cards.width + spacing)
    )
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height
    )

  override def onInit: Engine => Unit = engine =>
    engine.create(leftCard)
    engine.create(centerCard)
    engine.create(rightCard)
    super.onInit(engine)

  override def onUpdate: Engine => Unit = engine =>
    val playerHand = engine.gameModel.player(player).hand.cards
    val newLeftCard = playerHand.headOption
    if leftCard.card != newLeftCard then leftCard.card = newLeftCard

    val newCenterCard = playerHand.drop(1).headOption
    if centerCard.card != newCenterCard then centerCard.card = newCenterCard

    val newRightCard = playerHand.drop(2).headOption
    if rightCard.card != newRightCard then rightCard.card = newRightCard

    super.onUpdate(engine)
