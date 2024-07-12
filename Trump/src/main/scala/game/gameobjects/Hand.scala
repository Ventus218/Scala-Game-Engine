package game.gameobjects

import sge.core.*
import game.behaviours.*
import game.Values
import sge.core.behaviours.dimension2d.*
import game.Utils.gameModel
import game.Utils.toImagePath

class Hand(val player: String, position: Vector2D = (0, 0))
    extends Behaviour
    with Positionable:

  val leftCard: CardImage = new Behaviour
    with CardImage(_card = Option.empty, _show = false)
    with Positionable
    with PositionFollower(this, positionOffset = (-10, 0))
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height
    )

  val centerCard: CardImage = new Behaviour
    with CardImage(_card = Option.empty, _show = false)
    with Positionable
    with PositionFollower(this)
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height
    )

  val rightCard: CardImage = new Behaviour
    with CardImage(_card = Option.empty, _show = false)
    with Positionable
    with PositionFollower(this)
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height
    )

  override def onUpdate: Engine => Unit = engine =>
    val newLeftCard =
      engine.gameModel.player(player).hand.cards.headOption.map(_.toImagePath)
    if leftCard.imagePath != newLeftCard then leftCard.imagePath = newLeftCard

    val newCenterCard =
      engine.gameModel.player(player).hand.cards.headOption.map(_.toImagePath)
    if centerCard.imagePath != newCenterCard then
      centerCard.imagePath = newCenterCard

    val newRightCard =
      engine.gameModel.player(player).hand.cards.headOption.map(_.toImagePath)
    if rightCard.imagePath != newRightCard then
      rightCard.imagePath = newRightCard

    super.onUpdate(engine)
