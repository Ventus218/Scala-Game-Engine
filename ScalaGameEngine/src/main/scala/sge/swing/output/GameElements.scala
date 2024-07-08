package sge.swing.output

import java.awt.Graphics2D

object GameElements:
  /** Basic trait for manipulating and drawing game entities using Swing. The
    * main properties of the element (width, height) are mutable, and are
    * represented in game units.
    */
  trait GameElement:
    /** The width of the element in game units.
      *
      * @return
      *   the width
      */
    def elementWidth: Double

    /** Set the width of the element.
      *
      * @param w
      *   the new width. It can't be negative or 0.
      */
    def elementWidth_=(w: Double): Unit

    /** The height of the element in game units.
      *
      * @return
      *   the height
      */
    def elementHeight: Double

    /** Set the height of the element.
      *
      * @param h
      *   the new height. It can't be negative or 0.
      */
    def elementHeight_=(h: Double): Unit

    /** The function defining the operation to apply on the graphic context of
      * the Swing GUI. It accepts in input: the graphic context, and a tuple of
      * composed of the starting x screen position, the starting y screen
      * position, the pixel width of the element and the pixel height of the
      * element.
      */
    def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit

  /** Base implementation of GameElement.
    * @param width
    *   the width. It can't be negative or 0.
    * @param height
    *   the height. It can't be negative or 0.
    */
  abstract class BaseGameElement(
      private var width: Double,
      private var height: Double
  ) extends GameElement:
    elementWidth = width
    elementHeight = height
    override def elementWidth: Double = width
    override def elementWidth_=(w: Double): Unit =
      require(w > 0, "width must be positive")
      width = w

    override def elementHeight: Double = height
    override def elementHeight_=(h: Double): Unit =
      require(h > 0, "height must be positive")
      height = h
