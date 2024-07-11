package managers

import java.awt.{Color, Font}

/** Object containing all game general info regarding the arena
  */
object GameConstants:
  val screenSize:    (Int, Int) = (600, 900)
  val arenaWidth:    Double     = 10
  val pixelsPerUnit: Int        = (screenSize._1.toDouble / arenaWidth).toInt
  val arenaHeight:   Double     = screenSize._2.toDouble / pixelsPerUnit.toDouble
  
  val arenaRightBorder:  Double = arenaWidth / 2 - 1
  val arenaLeftBorder:   Double = -arenaRightBorder
  val arenaTopBorder:    Double = arenaHeight / 2 - 2
  val arenaBottomBorder: Double = - arenaHeight / 2 + 1
  val playerTopBorder:   Double = -1

  val scoreStorageKey:    String = "score"
  val topScoreStorageKey: String = "topScore"
  
  val buttonColor:     Color            = Color.decode("#DA1616")
  val playButtonColor: Color            = Color.decode("#1A5EFF")
  val buttonSize:      (Double, Double) = (2, 1)
  val buttonTextSize:  Double           = 0.7

  val titleSize:  Double = 1.2
  val titleColor: Color  = Color.white
  
  val scoreTextSize: Int  = 25
  val scoreTextFont: Font = Font("Arial", Font.BOLD, scoreTextSize)

  val missionTextSize:       Double = 0.8
  val missionTextLerpFactor: Double = 0.15
  val missionTextColor:      Color  = Color.yellow

  val yourScoreTextSize:  Double = 0.6
  val yourScoreTextColor: Color  = Color.decode("#CCCCCC")

  val gameoverTextSize:  Double = 1
  val gameoverTextColor: Color  = Color.white