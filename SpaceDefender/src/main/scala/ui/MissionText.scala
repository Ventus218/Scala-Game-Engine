package ui

import managers.GameConstants.*
import sge.core.*
import behaviours.dimension2d.*
import sge.swing.behaviours.ingame.TextRenderer
import sge.swing.output.Text.TextStyle
import util.VectorUtils

import java.awt.Color

object MissionText:
  private class FollowerText(text: String, offset: Vector2D, parent: Positionable) extends Behaviour
    with TextRenderer(
      text,
      missionTextSize,
      Color.yellow,
      fontStyle = TextStyle.Bold
    ) with PositionFollower(
      followed = parent,
      positionOffset = offset
    )
    with Positionable

import MissionText.*

class MissionText extends Behaviour with Positionable(arenaWidth, 0):
  private lazy val text1 = FollowerText("MISSION", (0, missionTextSize / 2), this)
  private lazy val text2 = FollowerText("START!", (0, -missionTextSize / 2), this)

  override def onStart: Engine => Unit =
    engine =>
      engine.create(text1)
      engine.create(text2)
      super.onInit(engine)

  override def onDeinit: Engine => Unit =
    engine =>
      engine.destroy(text1)
      engine.destroy(text2)
      super.onInit(engine)

  def show(): Unit =
    position = VectorUtils.lerp(position, (0, 0), missionTextLerpFactor)

  def hide(): Unit =
    position = VectorUtils.lerp(position, (-arenaWidth, 0), missionTextLerpFactor)