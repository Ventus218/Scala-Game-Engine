package sge.swing.output

import java.awt.{Color, Graphics2D}

/* Utility object for shapes */
object Shapes:
  import GameElements.*

  /** Basic trait for manipulating and drawing geometric shapes using Swing. The
    * main properties of the shape (width, height, and color) are mutable.
    */
  trait Shape extends GameElement:
    /** The color of the shape.
      * @return
      *   the color
      */
    def shapeColor: Color

    /** Set the color of the shape.
      * @param c
      *   the new color. It can't be null.
      */
    def shapeColor_=(c: Color): Unit

  /** Base implementation of BaseShape.
    * @param width
    *   the width. It can't be negative or 0.
    * @param height
    *   the height. It can't be negative or 0.
    * @param color
    *   the color. It can't be null.
    */
  private abstract class BaseShape(
      width: Double,
      height: Double,
      private var color: Color
  ) extends BaseGameElement(width, height)
      with Shape:
    shapeColor = color

    override def shapeColor: Color = color
    override def shapeColor_=(c: Color): Unit =
      require(c != null, "color can't be null")
      color = c

  /** A rectangular shape.
    */
  trait Rect extends Shape:
    override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
      g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillRect(posX, posY, w, h)

  /** A square shape.
    */
  trait Square extends Rect:
    override def elementHeight: Double = elementWidth
    override def elementHeight_=(h: Double): Unit = elementWidth = h

  /** An oval shape.
    */
  trait Oval extends Shape:
    override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
      g2d =>
        (posX, posY, w, h) =>
          g2d.setColor(shapeColor)
          g2d.fillOval(posX, posY, w, h)

  /** A circular shape.
    */
  trait Circle extends Oval:
    /** The radius of the circle in game units.
      * @return
      *   the radius
      */
    def shapeRadius: Double = elementWidth / 2

    /** Set the radius of the circle.
      * @param r
      *   the new radius. It can't be negative or 0.
      */
    def shapeRadius_=(r: Double): Unit =
      require(r > 0, "radius must be positive")
      elementWidth = r * 2
    override def elementHeight: Double = elementWidth
    override def elementHeight_=(h: Double): Unit = elementWidth = h

  /** Create a rectangular BaseShape.
    * @param width
    *   the width in game units
    * @param height
    *   the height in game units
    * @param color
    *   the color of the shape
    * @return
    *   a new Rect
    */
  def rect(width: Double, height: Double, color: Color): Rect =
    new BaseShape(width, height, color) with Rect

  /** Create a square BaseShape.
    * @param size
    *   the size in game units
    * @param color
    *   the color of the shape
    * @return
    *   a new Square
    */
  def square(size: Double, color: Color): Square =
    new BaseShape(size, size, color) with Square

  /** Create an oval BaseShape.
    * @param width
    *   the width in game units
    * @param height
    *   the height in game units
    * @param color
    *   the color of the shape
    * @return
    *   a new Oval
    */
  def oval(width: Double, height: Double, color: Color): Oval =
    new BaseShape(width, height, color) with Oval

  /** Create a circular BaseShape.
    * @param radius
    *   the radius in game units
    * @param color
    *   the color of the shape
    * @return
    *   a new Circle
    */
  def circle(radius: Double, color: Color): Circle =
    new BaseShape(radius * 2, radius * 2, color) with Circle
