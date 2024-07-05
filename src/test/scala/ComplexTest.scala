import SwingInputHandler.{*, given}
import java.awt.Color
import SwingIO.InputButton
import InputButton.*
import Dimensions2D.*
import SwingRenderers.*
import Physics2D.*
import Dimensions2D.Vector.*

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

  class PlayButton
      extends Behaviour
      with SwingButton(
        buttonText = "Play",
        _inputButtonTriggers = Set(MouseButton1, P)
      )
      with Positionable
      with SwingRectRenderer(20, 8, Color.gray):
    override def onButtonPressed: Engine => Unit = engine =>
      engine.loadScene(GameScene)

  class Obstacle(initX: Double, initY: Double, squareSide: Double)
      extends Behaviour
      with Positionable(initX, initY)
      with Scalable(1.0, 1.0)
      with SwingSquareRenderer(squareSide, Color.red)
      with RectCollider(squareSide, squareSide)

  class GameObject(
      initX: Double,
      initY: Double,
      circleRadius: Double,
      val movementVelocity: Double = 40
  ) extends Behaviour
      with Positionable(initX, initY)
      with SingleScalable(1.0)
      with SwingCircleRenderer(circleRadius, Color.blue)
      with CircleCollider(circleRadius)
      with SwingInputHandler
      with Velocity
      with Acceleration((0, -100)):
    var inputHandlers: Map[InputButton, Handler] = Map(
      D -> onMoveRight,
      A -> onMoveLeft,
      MouseButton1 -> onTeleport,
      Space -> onJump.onlyWhenPressed,
      R -> onReset
    )

    private def onTeleport(input: InputButton)(engine: Engine): Unit =
      val pointer = engine.io.asInstanceOf[SwingIO].scenePointerPosition()
      position = pointer
      velocity = (0, 0)

    private def onMoveRight(input: InputButton)(engine: Engine): Unit =
      position =
        position + Versor.right * movementVelocity * engine.deltaTimeSeconds

    private def onMoveLeft(input: InputButton)(engine: Engine): Unit =
      position =
        position - Versor.x * movementVelocity * engine.deltaTimeSeconds

    private var jumped = false
    private def onJump(input: InputButton)(engine: Engine): Unit =
      jumped = true

    private def onReset(input: InputButton)(engine: Engine): Unit =
      engine
        .find[Obstacle]()
        .foreach(engine.enable(_))

    override def onUpdate: Engine => Unit = (engine) =>
      val io = engine.io.asInstanceOf[SwingIO]
      val top = io.scenePosition((0, 0)).y - shapeHeight / 2
      val bottom = io.scenePosition(io.size).y + shapeHeight / 2
      val right = io.scenePosition(io.size).x - shapeWidth / 2
      val left = io.scenePosition((0, 0)).x + shapeWidth / 2

      if position.y > top then
        position = position.setY(top)
        velocity = velocity.setY(0)
      if position.y < bottom then
        position = position.setY(bottom)
        velocity = velocity.setY(0)

      if position.x > right then
        position = position.setX(right)
        velocity = velocity.setX(0)
      if position.x < left then
        position = position.setX(left)
        velocity = velocity.setX(0)

      if jumped then
        jumped = false
        velocity = velocity.setY(60)

      // collision
      engine
        .find[Obstacle]()
        .foreach(o => if collides(o) then engine.disable(o))

      super.onUpdate(engine)

  object MenuScene extends Scene:
    override def apply(): Iterable[Behaviour] =
      Seq(PlayButton())

  object GameScene extends Scene:
    override def apply(): Iterable[Behaviour] =
      Seq(
        GameObject(initX = 0, initY = -20, circleRadius = 5),
        Obstacle(initX = 0, initY = 0, squareSide = 5),
        Obstacle(initX = 10, initY = 20, squareSide = 25),
        Obstacle(initX = -20, initY = -30, squareSide = 10),
        Obstacle(initX = 40, initY = 20, squareSide = 2)
      )
