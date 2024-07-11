package model.behaviours.walls

import sge.swing.*
import model.behaviours.Wall
import config.Config.*
import java.awt.Color

private val boundOffset = 1
private val boundSize = 1

class TopBound
    extends Wall(SCENE_WIDTH, boundSize, position = (0, SCENE_HEIGHT / 2 + boundOffset))()
    with RectRenderer(SCENE_WIDTH, boundSize, Color.BLACK)

class LeftBound
    extends Wall(boundSize, SCENE_HEIGHT, position = (- SCENE_WIDTH / 2 - boundOffset, 0))()
    with RectRenderer(boundSize, SCENE_HEIGHT, Color.BLACK)

class BottomBound
    extends Wall(SCENE_WIDTH, boundSize, position = (0, - SCENE_HEIGHT / 2 - boundOffset))()
    with RectRenderer(SCENE_WIDTH, boundSize, Color.BLACK)

class RightBound
    extends Wall(boundSize, SCENE_HEIGHT, position = (SCENE_WIDTH / 2 + boundOffset, 0))()
    with RectRenderer(boundSize, SCENE_HEIGHT, Color.BLACK)
