package game.gameobjects

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable
import game.*
import model.Trump.*
import game.behaviours.*

class Deck(position: Vector2D)
    extends Behaviour
    with Positionable(position)
    with CardImage(
      _card = Option.empty,
      _show = false
    )
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height,
      priority = 10
    )
    with TextRenderer(
      text = "",
      size = Values.Text.size,
      color = Values.Text.color,
      priority = 20,
      offset = (0, 10)
    ):

  override def onStart: Engine => Unit = engine =>
    card = Some(engine.gameModel.deck.cards.head)
    super.onStart(engine)

  override def onUpdate: Engine => Unit = engine =>
    textContent = s"${engine.gameModel.deck.size}"
    if engine.gameModel.deck.size <= 0 then engine.destroy(this)
    super.onUpdate(engine)
