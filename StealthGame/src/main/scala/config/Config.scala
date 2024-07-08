package config
import java.awt.Color

object Config:
    val SCREEN_WIDTH: Int = 1200
    val SCREEN_HEIGHT: Int = 720
    val PIXEL_UNIT_RATIO: Int = 10

    val GREEN = Color(0, 125, 50)
    val BROWN = Color(125, 50, 0)

    val BUTTON_WIDTH: Double = 30
    val BUTTON_HEIGHT: Double = 5
    val BUTTON_OFFSET: Double = (BUTTON_HEIGHT + 1)
