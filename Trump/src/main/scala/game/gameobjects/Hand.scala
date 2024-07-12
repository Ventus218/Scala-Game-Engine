package game.gameobjects

import sge.core.*
import sge.core.behaviours.dimension2d.*
import sge.swing.*
import game.behaviours.*
import game.*
import model.Cards.*
import java.awt.Color

class Hand(
    val player: String,
    position: Vector2D = (0, 0),
    val spacing: Double = 0,
    val show: Boolean
) extends Behaviour
    with Positionable(position):

  val leftCard: CardImage = handCard(
    Versor2D.left * (Values.Dimensions.Cards.width + spacing)
  )
  val centerCard: CardImage = handCard()
  val rightCard: CardImage = handCard(
    Versor2D.right * (Values.Dimensions.Cards.width + spacing)
  )

  private def onCardClicked(engine: Engine, card: Card): Unit =
    if engine.gameModel.currentPlayer.info == player then
      engine.gameModel.playCard(card) match
        case Right(newGame) => engine.gameModel = newGame._1
        case Left(value)    => throw Exception(value.message)

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

  private def handCard(positionOffset: Vector2D = (0, 0)): CardImage =
    val width = Values.Dimensions.Cards.width
    val height = Values.Dimensions.Cards.height
    new Behaviour
      with CardImage(_card = Option.empty, _show = show)
      with Positionable
      with PositionFollower(
        this,
        positionOffset = positionOffset
      )
      with ChangeableImageRenderer(
        width = width,
        height = height
      )
      with RectRenderer(
        width = width,
        height = height,
        color = Color(0, 0, 0, 0) // Transparent
      )
      with Button:
      override def onButtonPressed: Engine => Unit = onCardClicked(_, card.get)