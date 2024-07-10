package config
import java.awt.Color
import sge.swing.input.InputButton
import java.awt.event.KeyEvent.*

object Config:
  val SCREEN_WIDTH: Int = 1280
  val SCREEN_HEIGHT: Int = 720
  val PIXEL_UNIT_RATIO: Int = 10

  val BUTTON_WIDTH: Double = 30
  val BUTTON_HEIGHT: Double = 5
  val BUTTON_OFFSET: Double = (BUTTON_HEIGHT + 2)