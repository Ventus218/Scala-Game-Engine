package ui

import managers.GameConstants.*
import sge.core.*
import behaviours.dimension2d.Positionable
import sge.swing.behaviours.ingame.TextRenderer
import sge.swing.output.Text.TextStyle
import util.VectorUtils

import java.awt.Color

class MissionText extends Behaviour with TextRenderer(
    "MISSION\nSTART!",
    missionTextSize,
    Color.yellow,
    fontStyle = TextStyle.Bold
  ) with Positionable(arenaWidth, 0):
  
  def show(): Unit =
    position = VectorUtils.lerp(position, (0, 0), missionTextLerpFactor)
  def hide(): Unit =
    position = VectorUtils.lerp(position, (-arenaWidth, 0), missionTextLerpFactor)