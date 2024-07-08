package sge.swing.behaviours.overlay

import sge.swing.*
import output.overlay.UIAnchor
import behaviours.Renderer
import java.awt.{Color, Font, Graphics2D}

/** Behaviour for rendering an text on a SwingIO. The text is positioned on the
  * screen based on its anchor point and its offset. By default the anchor is in
  * the top-left corner.
  */
trait UITextRenderer(
    private var text: String,
    private var font: Font,
    private var color: Color,
    var textAnchor: UIAnchor = UIAnchor.TopLeft,
    var textOffset: (Int, Int) = (0, 0)
) extends Renderer:
  require(font != null, "text font can't be null")
  textContent = text
  textColor = color

  import UIAnchor.*

  def textContent: String = text
  def textContent_=(txt: String): Unit =
    require(txt != null, "text content can't be null")
    text = txt
  def textSize: Int = font.getSize
  def textSize_=(s: Int): Unit =
    require(s > 0, "text size must be positive")
    font = Font(font.getFontName, font.getStyle, s)
  def textColor: Color = color
  def textColor_=(c: Color): Unit =
    require(c != null, "text color can't be null")
    color = c

  /** Compute the screen position relative to the anchor point.
    *
    * @param io
    *   the SwingIO
    * @param g2d
    *   the graphic context
    * @return
    *   the screen position of this UI element
    */
  private def anchoredPosition(io: SwingIO)(g2d: Graphics2D): (Int, Int) =
    val width: Int = g2d.getFontMetrics(font).stringWidth(textContent)
    val height: Int = textSize
    val screenWidth: Int = io.size._1
    val screenHeight: Int = io.size._2
    textAnchor match
      case TopLeft    => (0, height)
      case TopCenter  => ((screenWidth - width) / 2, height)
      case TopRight   => (screenWidth - width, height)
      case CenterLeft => (0, (screenHeight + height) / 2)
      case Center => ((screenWidth - width) / 2, (screenHeight + height) / 2)
      case CenterRight  => (screenWidth - width, (screenHeight + height) / 2)
      case BottomLeft   => (0, screenHeight)
      case BottomCenter => ((screenWidth - width) / 2, screenHeight)
      case BottomRight  => (screenWidth - width, screenHeight)

  override def renderer: SwingIO => Graphics2D => Unit = io =>
    g2d =>
      g2d.setFont(font)
      g2d.setPaint(color)
      val position = anchoredPosition(io)(g2d)
      g2d.drawString(
        text,
        position._1 + textOffset._1,
        position._2 + textOffset._2
      )
