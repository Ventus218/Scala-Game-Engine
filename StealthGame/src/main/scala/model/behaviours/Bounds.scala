package model.behaviours

import sge.swing.*
import model.behaviours.Wall
import config.Config.*
import java.awt.Color

private val boundSize = 1
private val boundOffset = 1
class TopBound
    extends Wall(SCENE_WIDTH, boundSize, (0, SCENE_HEIGHT / 2 + boundOffset))()

class LeftBound
    extends Wall(boundSize, SCENE_HEIGHT, (- SCENE_WIDTH / 2 - boundOffset, 0))()

class BottomBound
    extends Wall(SCENE_WIDTH, boundSize, (0, - SCENE_HEIGHT / 2 - boundOffset))()

class RightBound
    extends Wall(boundSize, SCENE_HEIGHT, (SCENE_WIDTH / 2 + boundOffset, 0))()
