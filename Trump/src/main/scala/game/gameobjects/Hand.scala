package game.gameobjects

import sge.core.*
import sge.core.behaviours.dimension2d.*
import sge.swing.*
import game.behaviours.*
import game.*
import model.Cards.*
import java.awt.Color
import model.TrumpResult

class Hand(
    val player: String,
    position: Vector2D = (0, 0),
    val spacing: Double = 0
) extends Behaviour
    with Positionable(position):

  var show = false

  val leftCard: CardImage = handCard(
    Versor2D.left * (Values.Dimensions.Cards.width + spacing)
  )
  val centerCard: CardImage = handCard()
  val rightCard: CardImage = handCard(
    Versor2D.right * (Values.Dimensions.Cards.width + spacing)
  )

  private def onCardClicked(engine: Engine, card: Card): Unit =
    if engine.gameModel.currentPlayer.info == player && show then
      (for
        playCardResult <- engine.gameModel.playCard(card).left.map(_.message)
        playerReadyButton <- engine
          .find[PlayerReadyButton](Values.Ids.playerReadyButton)
          .toRight("Didn't find the PlayerReadyButton")
      yield (playCardResult, playerReadyButton)) match
        case Left(errorMessage) => throw Exception(errorMessage)
        case Right(((game, Some(trumpResult)), playerReadyButton)) =>
          engine.storage.set(
            StorageKeys.gameResult,
            GameResult(game, trumpResult)
          )
          engine.loadScene(scenes.GameResult)
        case Right((playCardResult, playerReadyButton)) =>
          engine.gameModel = playCardResult._1
          show = false
          engine.enable(playerReadyButton)

  override def onInit: Engine => Unit = engine =>
    engine.create(leftCard)
    engine.create(centerCard)
    engine.create(rightCard)
    super.onInit(engine)

  override def onUpdate: Engine => Unit = engine =>
    val showHand = engine.gameModel.currentPlayer.info == player && show

    val playerHand = engine.gameModel.player(player).hand.cards
    val newLeftCard = playerHand.headOption
    if leftCard.card != newLeftCard then leftCard.card = newLeftCard
    leftCard.show = showHand

    val newCenterCard = playerHand.drop(1).headOption
    if centerCard.card != newCenterCard then centerCard.card = newCenterCard
    centerCard.show = showHand

    val newRightCard = playerHand.drop(2).headOption
    if rightCard.card != newRightCard then rightCard.card = newRightCard
    rightCard.show = showHand

    super.onUpdate(engine)

  private def handCard(positionOffset: Vector2D = (0, 0)): CardImage =
    val width = Values.Dimensions.Cards.width
    val height = Values.Dimensions.Cards.height
    new Behaviour
      with CardImage(_card = Option.empty, false)
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
      override def onButtonPressed: Engine => Unit = engine =>
        card match
          case None       => ()
          case Some(card) => onCardClicked(engine, card)
