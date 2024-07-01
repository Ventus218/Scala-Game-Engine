import SwingInputHandler.{*, given}
import java.awt.Color
import SwingIO.InputButton
import InputButton.*
import Dimensions2D.*
import SwingRenderers.*
import Physics2D.*

object ComplexTest:
  extension (e: Engine) def swingIO: SwingIO = e.io.asInstanceOf[SwingIO]

  @main
  def test: Unit =

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

    engine.run(MenuScene)

  trait Velocity(var velX: Double = 0, var velY: Double = 0)
      extends Positionable:
    override def onUpdate: Engine => Unit = (engine) =>
      x += velX * engine.deltaTimeSeconds
      y += velY * engine.deltaTimeSeconds
      super.onUpdate(engine)

  trait Acceleration(var accX: Double = 0, var accY: Double = 0)
      extends Velocity:
    override def onUpdate: Engine => Unit = (engine) =>
      velX = velX + (accX * engine.deltaTimeSeconds)
      velY = velY + (accY * engine.deltaTimeSeconds)
      super.onUpdate(engine)

  class MenuObj extends Behaviour with SwingInputHandler:
    var inputHandlers: Map[InputButton, Handler] = Map(
      P -> onPlay
    )

    private def onPlay(input: InputButton)(engine: Engine): Unit =
      engine.loadScene(GameScene)

  class Obstacle(initX: Double, initY: Double, squareSide: Double)
      extends Behaviour
      with Positionable(initX, initY)
      with Scalable
      with SwingSquareRenderer(squareSide, Color.red)
      with RectCollider(squareSide, squareSide)

  class GameObject(
      initX: Double,
      initY: Double,
      circleRadius: Double,
      val movementVelocity: Double = 40
  ) extends Behaviour
      with Positionable(initX, initY)
      with SingleScalable
      with SwingCircleRenderer(circleRadius, Color.blue)
      with CircleCollider(circleRadius)
      with SwingInputHandler
      with Velocity
      with Acceleration(accY = -100):
    var inputHandlers: Map[InputButton, Handler] = Map(
      D -> onMoveRight,
      A -> onMoveLeft,
      MouseButton1 -> onTeleport,
      Space -> onJump.onlyWhenPressed,
      R -> onReset
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

    private def onReset(input: InputButton)(engine: Engine): Unit =
      engine
        .find[Obstacle]()
        .foreach(engine.enable(_))

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

      // collision
      engine
        .find[Obstacle]()
        .foreach(o => if collides(o) then engine.disable(o))

      super.onUpdate(engine)

  object MenuScene extends Scene:
    override def apply(): Iterable[Behaviour] =
      Seq(MenuObj())

  object GameScene extends Scene:
    override def apply(): Iterable[Behaviour] =
      Seq(
        GameObject(initX = 0, initY = -20, circleRadius = 5),
        Obstacle(initX = 0, initY = 0, squareSide = 5),
        Obstacle(initX = 10, initY = 20, squareSide = 25),
        Obstacle(initX = -20, initY = -30, squareSide = 10),
        Obstacle(initX = 40, initY = 20, squareSide = 2)
      )
