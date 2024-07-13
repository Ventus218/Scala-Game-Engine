package ui

import managers.GameConstants.*
import util.VectorUtils

/** The starting text of 'Mission Start'
 */
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