package model.behaviours

import sge.core.*
import behaviours.dimension2d.*
import behaviours.physics2d.RectCollider
import sge.swing.*
import config.Config.VISUAL_RANGE_COLOR

/** Visual range of a character
  *
  * @param width
  *   Width of the visual range
  * @param height
  *   Height of the visual range
  * @param follower
  *   character that has this visual range
  */
class VisualRange(width: Double, height: Double, follower: Character)
    extends Behaviour
    with Positionable
    with RectRenderer(width, height, VISUAL_RANGE_COLOR, priority = 99)
    with RectCollider(width, height)
    with PositionFollower(follower)
    with Scalable
