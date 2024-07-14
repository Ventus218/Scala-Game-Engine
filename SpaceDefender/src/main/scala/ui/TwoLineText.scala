package ui

import managers.GameConstants.*
import sge.core.*
import behaviours.dimension2d.*
import sge.swing.*
import output.Text.TextStyle

import java.awt.Color

object TwoLineText:
  /** A text that follows an object.
    * @param text
    *   the content
    * @param size
    *   the text size in game units
    * @param color
    *   the text color
    * @param offset
    *   the offset with the followed object
    * @param parent
    *   the followed object
    */
  class FollowerText(
      text: String,
      size: Double,
      color: Color,
      offset: Vector2D,
      parent: Positionable
  ) extends Behaviour
    with TextRenderer(
      text,
      size,
      color,
      fontStyle = TextStyle.Bold
    ) with PositionFollower(
      followed = parent,
      positionOffset = offset
    )
    with Positionable

import TwoLineText.*

/** A Text renderer with a text distributed in two lines.
  * @param pos
  *   the position
  * @param text1
  *   the first line
  * @param text2
  *   the second line
  * @param size
  *   the text size
  * @param color
  *   the text color
  */
class TwoLineText(
    pos: Vector2D,
    text1: String,
    text2: String,
    size: Double,
    color: Color = Color.white
) extends Behaviour
  with Positionable(pos):

  val line1: FollowerText = FollowerText(text1, size, color, (0, size / 2), this)
  val line2: FollowerText = FollowerText(text2, size, color, (0, -size / 2), this)

  override def onStart: Engine => Unit =
    engine =>
      engine.create(line1)
      engine.create(line2)
      super.onInit(engine)

  override def onDeinit: Engine => Unit =
    engine =>
      engine.destroy(line1)
      engine.destroy(line2)
      super.onInit(engine)
