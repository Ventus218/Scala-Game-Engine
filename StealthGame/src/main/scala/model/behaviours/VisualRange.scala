package model.behaviours

import sge.core.*
import behaviours.dimension2d.*
import behaviours.physics2d.RectCollider
import sge.swing.*
import config.Config.VISUAL_RANGE_COLOR

class VisualRange(width: Double, height: Double, follower: Positionable)
    extends Behaviour
    with Positionable
    with RectRenderer(width, height, VISUAL_RANGE_COLOR, priority = 99)
    with RectCollider(width, height)
    with PositionFollower(follower)
    with Scalable