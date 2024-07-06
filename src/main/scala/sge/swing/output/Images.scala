package sge.swing.output

import java.awt.{Graphics2D, Image}
import javax.imageio.ImageIO

/* Utility object for images */
object Images:
  import GameElements.*

  /** Basic trait for manipulating and drawing images using Swing. The main
    * properties of the element (width, height) are mutable, and are represented
    * in game units.
    */
  trait SwingImage extends SwingGameElement:
    /** The image to draw
      */
    val image: Image
    override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
      g2d =>
        (posX, posY, w, h) =>
          val img = image.getScaledInstance(w, h, Image.SCALE_DEFAULT)
          g2d.drawImage(img, posX, posY, null)

  private class SingleSwingImage(
      override val image: Image,
      width: Double,
      height: Double
  ) extends BaseSwingGameElement(width, height)
      with SwingImage

  /** Create a SwingImage from an image path
    * @param imgPath
    *   the path of the image file. Must be in a resource folder
    * @param width
    *   the wanted width of the image in game units
    * @param height
    *   the wanted height of the image in game units
    * @return
    *   a new SwingImage
    */
  def simpleImage(
      imgPath: String,
      width: Double,
      height: Double
  ): SwingImage =
    SingleSwingImage(
      ImageIO.read(getClass.getResourceAsStream(s"/$imgPath")),
      width,
      height
    )
