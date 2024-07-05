package sge.swing.output

import java.awt.{Color, Font, FontMetrics, Graphics2D}

/* Utility object for images */
object Text:
  import GameElements.*

  /** The style of the font. Can be Plain, Bold or Italic.
    * @param style
    *   the Swing keycode for the style
    */
  enum TextStyle(val style: Int):
    case Plain extends TextStyle(Font.PLAIN)
    case Bold extends TextStyle(Font.BOLD)
    case Italic extends TextStyle(Font.ITALIC)

  /** The font family name
    */
  type FontName = String

  /** Basic trait for manipulating and drawing texts using Swing. The main
    * properties of the element (content, size, style, font) are mutable.
    * Dimensions are represented in game units.
    */
  trait SwingText extends SwingGameElement:
    /** The content of the text.
      * @return
      *   the text
      */
    def textContent: String

    /** Set the content of the text.
      * @param text
      *   the new text
      */
    def textContent_=(text: String): Unit

    /** The style of the text.
      * @return
      *   the style
      */
    def textStyle: TextStyle

    /** Set the style of the text.
      * @param style
      *   the new style
      */
    def textStyle_=(style: TextStyle): Unit

    /** The font family of the text.
      * @return
      *   the font family
      */
    def textFont: FontName

    /** Set the font family of the text.
      * @param font
      *   the new font family
      */
    def textFont_=(font: FontName): Unit

    /** The color of the text.
      * @return
      *   the color
      */
    def textColor: Color

    /** Set the color of the text.
      * @param color
      *   the new color
      */
    def textColor_=(color: Color): Unit

    override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
      g2d =>
        (posX, posY, w, h) =>
          g2d.setFont(Font(textFont, textStyle.style, h))
          g2d.setPaint(textColor)
          g2d.drawString(textContent, posX, posY + h)

  /** Simple implementation of SwingText, that represents a one-line text. Its
    * width cannot be modified, and is automatically computed given the font
    * size and the text.
    * @param text
    *   the content
    * @param size
    *   the size in game-units
    * @param color
    *   the color
    * @param font
    *   the font family
    * @param style
    *   the font style
    */
  private class OneLineSwingText(
      private var text: String,
      private var size: Double,
      private var color: Color,
      private var font: FontName,
      private var style: TextStyle
  ) extends BaseSwingGameElement(size, size)
      with SwingText:
    import java.awt.font.FontRenderContext
    textContent = text
    textColor = color
    override def textContent: String = text
    override def textContent_=(text: String): Unit =
      require(text != null, "text content can't be null")
      this.text = text
    override def textStyle: TextStyle = style
    override def textStyle_=(style: TextStyle): Unit = this.style = style
    override def textFont: FontName = font
    override def textFont_=(font: FontName): Unit = this.font = font
    override def textColor: Color = color
    override def textColor_=(color: Color): Unit =
      require(color != null, "text color can't be null")
      this.color = color
    override def elementWidth: Double =
      val dummyFont = Font(textFont, textStyle.style, 64)
      val fontRenderContext =
        FontRenderContext(dummyFont.getTransform, true, true)
      val ratio: Double = dummyFont
        .getStringBounds(textContent, fontRenderContext)
        .getWidth / 64
      elementHeight * ratio

  /** Create a one-line text game element.
    * @param text
    *   the content
    * @param size
    *   the size in game-units
    * @param color
    *   the color
    * @param font
    *   the font family
    * @param style
    *   the font style
    * @return
    *   the SwingText
    */
  def oneLineText(
      text: String,
      size: Double,
      color: Color,
      font: FontName = "Arial",
      style: TextStyle = TextStyle.Plain
  ): SwingText =
    OneLineSwingText(text, size, color, font, style)
