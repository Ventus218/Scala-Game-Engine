package managers
import sge.core.*
object GameManager:
  def screenSize: (Int, Int) = (600, 900)
  def arenaWidth: Double = 10
  def pixelsPerUnit: Int = (screenSize._1.toDouble / arenaWidth).toInt
  def arenaHeight: Double = screenSize._2.toDouble / pixelsPerUnit.toDouble

