package game.gameobjects

import sge.core.*
import game.behaviours.*
import game.*
import sge.core.behaviours.dimension2d.*
import model.Cards.*

class Field(
    id: String,
    position: Vector2D = (0, 0),
    val spacing: Double = 0
) extends Behaviour
    with Identifiable(id)
    with Positionable(position):

  val leftCard: CardImage = fieldCard(
    Versor2D.left * (Values.Dimensions.Cards.width / 2 + spacing)
  )

  def playCard(card: Card): Engine => Unit = engine =>
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
        engine.enable(playerReadyButton)

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
