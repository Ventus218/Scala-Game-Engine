package game.gameobjects

import sge.core.*
import game.behaviours.*
import game.*
import sge.core.behaviours.dimension2d.*
import model.Cards.*
import sge.core.metrics.Vector2D.Versor2D.right

class Field(
    id: String,
    position: Vector2D = (0, 0),
    val spacing: Double = 0
) extends Behaviour
    with Identifiable(id)
    with Positionable(position):

  // The left card will always reflect the actual game model
  val leftCard: CardImage = fieldCard(
    Versor2D.left * (Values.Dimensions.Cards.width / 2 + spacing)
  )

  // The right card will hold the card before it is actually played to let both player see it
  val rightCard: CardImage = fieldCard(
    Versor2D.right * (Values.Dimensions.Cards.width / 2 + spacing)
  )

  def playCard(card: Card): Engine => Unit = engine =>
    engine.gameModel.field.size match
      case 0 => _playCard(card)(engine)
      case _ => rightCard.card = Some(card)

    engine
      .find[PlayerReadyButton](Values.Ids.playerReadyButton) match
      case None => throw Exception("Unable to find the PlayerReadyButton")
      case Some(playerReadyButton) => engine.enable(playerReadyButton)

  def endOfTurn: Engine => Unit = engine =>
    rightCard.card match
      case Some(card) =>
        _playCard(card)(engine)
        rightCard.card = None
      case None => ()

  private def _playCard(card: Card): Engine => Unit =
    engine =>
      engine.gameModel.playCard(card) match
        case Left(error) => throw Exception(error.message)
        case Right(game, trumpResult) =>
          engine.gameModel = game
          trumpResult match
            case None => ()
            case Some(result) =>
              engine.storage.set(
                StorageKeys.gameResult,
                GameResult(game, result)
              )
              engine.loadScene(scenes.GameResult)

  override def onInit: Engine => Unit = engine =>
    engine.create(leftCard)
    engine.create(rightCard)
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

  override def onDeinit: Engine => Unit = engine =>
    engine.destroy(leftCard)
    engine.destroy(rightCard)
    super.onDeinit(engine)
