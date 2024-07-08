package gamebehaviours
import sge.core.*
import sge.swing.*
import config.Config.*
import sge.core.behaviours.dimension2d.Positionable

class BackgroundImage(path: String)
    extends Behaviour
    with ImageRenderer(
      path,
      SCREEN_WIDTH / PIXEL_UNIT_RATIO,
      SCREEN_HEIGHT / PIXEL_UNIT_RATIO
    )
    with Positionable
