package model.behaviours.walls

import sge.swing.*
import model.behaviours.Wall
import config.Config.*
import java.awt.Color

private val boundSize = 1

class TopBound
    extends Wall(SCENE_WIDTH, boundSize, (0, SCENE_HEIGHT / 2))()

class LeftBound
    extends Wall(boundSize, SCENE_HEIGHT, (- SCENE_WIDTH / 2, 0))()

class BottomBound
    extends Wall(SCENE_WIDTH, boundSize, (0, - SCENE_HEIGHT / 2))()

class RightBound
    extends Wall(boundSize, SCENE_HEIGHT, (SCENE_WIDTH / 2, 0))()
