package model.behaviours

import sge.swing.*
import model.behaviours.Wall
import config.Config.*
import java.awt.Color

private val boundSize = 1
private val boundOffset = 1

/** Top bound of the Scene
  */
class TopBound
    extends Wall(SCENE_WIDTH, boundSize, (0, SCENE_HEIGHT / 2 + boundOffset))()

/** Left bound of the Scene
  */
class LeftBound
    extends Wall(boundSize, SCENE_HEIGHT, (-SCENE_WIDTH / 2 - boundOffset, 0))()

/** Bottom bound of the Scene
  */
class BottomBound
    extends Wall(SCENE_WIDTH, boundSize, (0, -SCENE_HEIGHT / 2 - boundOffset))()

/** Right bound of the Scene
  */
class RightBound
    extends Wall(boundSize, SCENE_HEIGHT, (SCENE_WIDTH / 2 + boundOffset, 0))()
