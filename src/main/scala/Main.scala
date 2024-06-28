import SwingRenderers.SwingCircleRenderer
import SwingInputHandler.{*, given}
import Physics2D.CircleCollider
import java.awt.Color
import SwingIO.InputButton
import InputButton.*
import Dimensions2D.Positionable
import Dimensions2D.SingleScalable

extension (e: Engine) def swingIO: SwingIO = e.io.asInstanceOf[SwingIO]

object ComplexTest:

  @main def main(): Unit =
    val swingIO = SwingIO
      .withTitle("Test")
      .withSize(400, 400)
      .withPixelsPerUnitRatio(5)
      .build()
    val engine = Engine(swingIO, Storage(), fpsLimit = 60)
    val testScene = () => Seq()

  class Circle(renderRadius: Double, colliderRadius: Double, color: Color)
      extends Behaviour
      with Positionable(0, 0)
      with SingleScalable
      with SwingCircleRenderer(renderRadius, color)
      with SwingInputHandler
      with CircleCollider(colliderRadius):
    var inputHandlers: Map[InputButton, Handler] = Map(
      // MouseButton1 -> onTeleport
    )

  // private def onTeleport(inputButton: InputButton): Unit =
