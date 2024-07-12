package game.behaviours

import sge.core.*
import sge.swing.*
import sge.swing.behaviours.Renderer
import output.Images.*
import sge.core.behaviours.dimension2d.Positionable
import java.awt.Graphics2D
import game.Values
import model.Cards.*
import game.*

trait CardImage(
    private var _card: Option[Card],
    private var _show: Boolean,
    val backsideCardImagePath: String = Values.ImagePaths.cardBackside
) extends ChangeableImageRenderer:

  card = _card
  def card: Option[Card] = _card
  def card_=(newValue: Option[Card]) =
    _card = newValue
    show = show // trigger imagePath update

  show = _show
  def show: Boolean = _show
  def show_=(newValue: Boolean) =
    _show = newValue
    _show match
      case true =>
        imagePath = _card.map(_.toImagePath)
      case false =>
        imagePath = Some(backsideCardImagePath)
