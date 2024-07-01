import SwingRenderers.SwingCircleRenderer
import SwingInputHandler.{*, given}
import Physics2D.CircleCollider
import java.awt.Color
import SwingIO.InputButton
import InputButton.*
import Dimensions2D.Positionable
import Dimensions2D.SingleScalable

object ComplexTest:
  extension (e: Engine) def swingIO: SwingIO = e.io.asInstanceOf[SwingIO]

  @main def main: Unit =
    import Dimensions2D.*
    import SwingRenderers.SwingSquareRenderer

    val io = SwingIO
      .withTitle("Test")
      .withSize((600, 400))
      .withPixelsPerUnitRatio(5)
      .build()
    val engine = Engine(
      io,
      Storage(),
      fpsLimit = 60
    )

    engine.run: () =>
      Seq(GameObject())

    class GameObject(val movementVelocity: Double = 40)
        extends Behaviour
        with Positionable
        with SwingSquareRenderer(2, Color.blue)
        with SwingInputHandler
        with Velocity
        with Acceleration(accY = -100):
      var inputHandlers: Map[InputButton, Handler] = Map(
        D -> onMoveRight,
        A -> onMoveLeft,
        MouseButton1 -> onTeleport,
        Space -> onJump.onlyWhenPressed
      )

      private def onTeleport(input: InputButton)(engine: Engine): Unit =
        val pointer = engine.io.asInstanceOf[SwingIO].scenePointerPosition()
        x = pointer._1
        y = pointer._2
        velX = 0
        velY = 0

      private def onMoveRight(input: InputButton)(engine: Engine): Unit =
        x += movementVelocity * engine.deltaTimeSeconds

      private def onMoveLeft(input: InputButton)(engine: Engine): Unit =
        x -= movementVelocity * engine.deltaTimeSeconds

      private var jumped = false
      private def onJump(input: InputButton)(engine: Engine): Unit =
        jumped = true

      override def onUpdate: Engine => Unit = (engine) =>
        val io = engine.io.asInstanceOf[SwingIO]
        val top = io.scenePosition((0, 0))._2 - shapeHeight / 2
        val bottom = io.scenePosition(io.size)._2 + shapeHeight / 2
        val right = io.scenePosition(io.size)._1 - shapeWidth / 2
        val left = io.scenePosition((0, 0))._1 + shapeWidth / 2

        if y > top then
          y = top
          velY = 0
        if y < bottom then
          y = bottom
          velY = 0

        if x > right then
          x = right
          velX = 0
        if x < left then
          x = left
          velX = 0

        if jumped then
          jumped = false
          velY = 60

        super.onUpdate(engine)

trait Velocity(var velX: Double = 0, var velY: Double = 0) extends Positionable:
  override def onUpdate: Engine => Unit = (engine) =>
    x += velX * engine.deltaTimeSeconds
    y += velY * engine.deltaTimeSeconds
    super.onUpdate(engine)

trait Acceleration(var accX: Double = 0, var accY: Double = 0) extends Velocity:
  override def onUpdate: Engine => Unit = (engine) =>
    velX = velX + (accX * engine.deltaTimeSeconds)
    velY = velY + (accY * engine.deltaTimeSeconds)
    super.onUpdate(engine)
