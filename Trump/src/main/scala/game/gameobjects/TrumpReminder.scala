package game.gameobjects

import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import game.*
import game.behaviours.*
import model.Cards.*
import sge.core.behaviours.dimension2d.PositionFollower
import sge.swing.behaviours.ingame.TextRenderer
import sge.swing.behaviours.ingame.RectRenderer
import java.awt.Color

class TrumpReminder(position: Vector2D)
    extends Behaviour
    with Positionable(position)
    with TextRenderer(
      "Trump suit: ",
      size = Values.Text.size * 4 / 5,
      color = Values.Text.color
    ):

  override def onStart: Engine => Unit = engine =>
    val trumpAce = Card(engine.gameModel.trumpSuit, Ace)
    engine.create(
      new Behaviour
        with Positionable
        with PositionFollower(this, positionOffset = Versor2D.right * 10)
        with CardImage(Some(trumpAce), _show = true)
        with ChangeableImageRenderer(
          width = Values.Dimensions.Cards.width / 2,
          height = Values.Dimensions.Cards.height / 2
        )
    )
    super.onStart(engine)
