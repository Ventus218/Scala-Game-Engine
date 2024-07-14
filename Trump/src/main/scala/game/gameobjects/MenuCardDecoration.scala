package game.gameobjects

import sge.core.*
import sge.swing.*
import game.behaviours.CardImage
import model.Cards.*
import game.behaviours.ChangeableImageRenderer
import game.Values
import sge.core.behaviours.dimension2d.Positionable

class MenuCardDecoration(card: Card, position: Vector2D, rotation: Angle)
    extends Behaviour
    with Positionable(position)
    with CardImage(Some(card), _show = true)
    with ChangeableImageRenderer(
      width = Values.Dimensions.Cards.width,
      height = Values.Dimensions.Cards.height,
      rotation = rotation
    )
