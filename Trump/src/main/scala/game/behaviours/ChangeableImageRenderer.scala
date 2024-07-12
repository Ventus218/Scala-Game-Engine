package game.behaviours

import sge.core.*
import sge.swing.*
import sge.swing.behaviours.Renderer
import output.Images.*
import sge.core.behaviours.dimension2d.Positionable
import java.awt.Graphics2D
import game.Values

trait ChangeableImageRenderer(
    val initialImagePath: Option[String] = Option.empty,
    val width: Double,
    val height: Double,
    val offset: Vector2D = (0, 0),
    val rotation: Angle = 0.degrees,
    val priority: Int = 0
) extends Renderer
    with Positionable:

  private var _imagePath: Option[String] = Option.empty
  
  imagePath = initialImagePath
  def imagePath: Option[String] = _imagePath
  def imagePath_=(newValue: Option[String]) =
    _imagePath = newValue
    _imagePath match
      case None => ()
      case Some(path) =>
        imageRenderer = Some(
          new Behaviour
            with ImageRenderer(path, width, height, offset, rotation, priority)
            with Positionable(position)
        )

  private var imageRenderer: Option[ImageRenderer] = Option.empty

  def imageHeight: Double = imageRenderer.map(_.imageHeight).getOrElse(height)
  def imageHeight_=(newValue: Double): Unit =
    imageRenderer.map(_.imageHeight = newValue)

  def imageWidth: Double = imageRenderer.map(_.imageWidth).getOrElse(width)
  def imageWidth_=(newValue: Double): Unit =
    imageRenderer.map(_.imageWidth = newValue)

  override def renderer: SwingIO => Graphics2D => Unit = imageRenderer match
    case Some(r) => r.renderer;
    case None    => _ => _ => ()

  // Relaying engine calls to internal renderer
  override def onInit: Engine => Unit = engine =>
    imageRenderer.map(_.onInit(engine))
    super.onInit(engine)

  override def onStart: Engine => Unit = engine =>
    imageRenderer.map(_.onStart(engine))
    super.onStart(engine)

  override def onEarlyUpdate: Engine => Unit = engine =>
    imageRenderer.map(_.onEarlyUpdate(engine))
    super.onEarlyUpdate(engine)

  override def onUpdate: Engine => Unit = engine =>
    imageRenderer.map(_.onUpdate(engine))
    super.onUpdate(engine)

  override def onLateUpdate: Engine => Unit = engine =>
    imageRenderer.map(_.onLateUpdate(engine))
    super.onLateUpdate(engine)

  override def onDeinit: Engine => Unit = engine =>
    imageRenderer.map(_.onDeinit(engine))
    super.onDeinit(engine)

  override def onEnabled: Engine => Unit = engine =>
    imageRenderer.map(_.onEnabled(engine))
    super.onEnabled(engine)

  override def onDisabled: Engine => Unit = engine =>
    imageRenderer.map(_.onDisabled(engine))
    super.onDisabled(engine)
