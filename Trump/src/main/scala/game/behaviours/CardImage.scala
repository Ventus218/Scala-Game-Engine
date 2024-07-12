package game.behaviours

import sge.core.*
import sge.swing.*
import output.Images.*
import sge.core.behaviours.dimension2d.Positionable
import java.awt.Graphics2D
import sge.swing.behaviours.Renderer

trait CardImage(
    val cardImagePath: String,
    var show: Boolean,
    val width: Double,
    val height: Double,
    val backsideCardImagePath: String = "cards/backside.png",
    val offset: Vector2D = (0, 0),
    val rotation: Angle = 0.degrees
) extends Renderer
    with Positionable:

  private val backside: Image =
    simpleImage(backsideCardImagePath, width, height)

  private val frontsideRenderer: ImageRenderer = new Behaviour
    with ImageRenderer(cardImagePath, width, height)
    with Positionable(position)

  private val backsideRenderer: ImageRenderer = new Behaviour
    with ImageRenderer(backsideCardImagePath, width, height)
    with Positionable(position)

  override def renderer: SwingIO => Graphics2D => Unit = show match
    case true  => frontsideRenderer.renderer
    case false => backsideRenderer.renderer

  // Relaying engine calls to internal renderers
  override def onInit: Engine => Unit = engine =>
    frontsideRenderer.onInit(engine)
    backsideRenderer.onInit(engine)
    super.onInit(engine)

  override def onStart: Engine => Unit = engine =>
    frontsideRenderer.onStart(engine)
    backsideRenderer.onStart(engine)
    super.onStart(engine)

  override def onEarlyUpdate: Engine => Unit = engine =>
    frontsideRenderer.onEarlyUpdate(engine)
    backsideRenderer.onEarlyUpdate(engine)
    super.onEarlyUpdate(engine)

  override def onUpdate: Engine => Unit = engine =>
    frontsideRenderer.onUpdate(engine)
    backsideRenderer.onUpdate(engine)
    super.onUpdate(engine)

  override def onLateUpdate: Engine => Unit = engine =>
    frontsideRenderer.onLateUpdate(engine)
    backsideRenderer.onLateUpdate(engine)
    super.onLateUpdate(engine)

  override def onDeinit: Engine => Unit = engine =>
    frontsideRenderer.onDeinit(engine)
    backsideRenderer.onDeinit(engine)
    super.onDeinit(engine)

  override def onEnabled: Engine => Unit = engine =>
    frontsideRenderer.onEnabled(engine)
    backsideRenderer.onEnabled(engine)
    super.onEnabled(engine)

  override def onDisabled: Engine => Unit = engine =>
    frontsideRenderer.onDisabled(engine)
    backsideRenderer.onDisabled(engine)
    super.onDisabled(engine)
