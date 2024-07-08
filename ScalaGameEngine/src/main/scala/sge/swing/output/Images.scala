package sge.swing.output

import java.awt.{Graphics2D, Image as AWTImage}
import javax.imageio.ImageIO

/* Utility object for images */
object Images:
  import GameElements.*

  case class ImageScaler(path: String):
    val baseImage: AWTImage = ImageIO.read(getClass.getResourceAsStream(s"/$path"))
    var imageCache: Option[(AWTImage, Int, Int)] = Option.empty
    def resize(width: Int, height: Int): AWTImage =
      if !imageCache.exists((_, w, h) => width == w && height == h) then
        imageCache = Option(
          baseImage.getScaledInstance(width, height, AWTImage.SCALE_DEFAULT),
          width,
          height
        )
      imageCache.map((img, _, _) => img).get

  /** Basic trait for manipulating and drawing images using Swing. The main
    * properties of the element (width, height) are mutable, and are represented
    * in game units.
    */
  trait Image extends GameElement:
    /** The image to draw
      */
    val image: AWTImage
    override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
      g2d =>
        (posX, posY, w, h) =>
          val img = image.getScaledInstance(w, h, AWTImage.SCALE_DEFAULT)
          g2d.drawImage(img, posX, posY, null)

  private class SingleImage(
      override val image: AWTImage,
      width: Double,
      height: Double
  ) extends BaseGameElement(width, height)
      with Image

  /** Create a Image from an image path
    * @param imgPath
    *   the path of the image file. Must be in a resource folder
    * @param width
    *   the wanted width of the image in game units
    * @param height
    *   the wanted height of the image in game units
    * @return
    *   a new Image
    */
  def simpleImage(
      imgPath: String,
      width: Double,
      height: Double
  ): Image =
    SingleImage(
      ImageIO.read(getClass.getResourceAsStream(s"/$imgPath")),
      width,
      height
    )
