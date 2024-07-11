package ui

import managers.GameConstants.*
import sge.core.*
import util.VectorUtils


class MissionText extends TwoLineText(
  (arenaWidth, 0),
  "MISSION", "START!",
  missionTextSize,
  missionTextColor
):
  def show(): Unit =
    position = VectorUtils.lerp(position, (0, 0), missionTextLerpFactor)

  def hide(): Unit =
    position = VectorUtils.lerp(position, (-arenaWidth, 0), missionTextLerpFactor)