package game.behaviours

import sge.core.*
import sge.swing.*
import sge.swing.behaviours.Renderer
import output.Images.*
import sge.core.behaviours.dimension2d.Positionable
import java.awt.Graphics2D
import game.Values

trait CardImage(
    private var _show: Boolean,
    val backsideCardImagePath: String = Values.ImagePaths.cardBackside
) extends ChangeableImageRenderer:

  private var originalImagePath: Option[String] = Option.empty

  def show: Boolean = _show
  def show_=(newValue: Boolean) =
    _show = newValue
    _show match
      case true =>
        imagePath = originalImagePath
      case false =>
        originalImagePath = imagePath
        imagePath = Some(backsideCardImagePath)
