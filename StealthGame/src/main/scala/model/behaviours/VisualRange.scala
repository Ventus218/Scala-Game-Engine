package model.behaviours

import sge.core.*
import behaviours.dimension2d.{Positionable, Scalable}
import behaviours.physics2d.RectCollider
import sge.swing.*
import java.awt.Color
import sge.core.behaviours.dimension2d.PositionFollower

class VisualRange(width: Double, height: Double, follower: Positionable) extends Behaviour
    with Positionable
    with RectRenderer(width, height, Color.BLUE)
    with RectCollider(width, height)
    with PositionFollower(follower)
    with Scalable
