package game.gameobjects

import sge.core.*
import sge.swing.*
import sge.core.behaviours.dimension2d.Positionable
import game.Values

class GameTitle(title: String = "Trump!")
    extends Behaviour
    with Positionable(Versor2D.up * 20)
    with TextRenderer(title, size = 10, Values.Text.color)
