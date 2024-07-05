import SwingRenderers.SwingRectRenderer
import SwingIO.*
import SwingInputHandler.{*, given}
import Dimensions2D.Vector.*

trait SwingButton(
    var buttonText: String = "",
    var inputButtonTriggers: Set[InputButton] = Set(InputButton.MouseButton1)
) extends Behaviour
    with SwingRectRenderer
    with SwingInputHandler:

  var inputHandlers: Map[InputButton, Handler] =
    /* Apparently needs to be explicit about the type to let the conversion work */
    val tuples: Set[(InputButton, Handler)] =
      inputButtonTriggers.map(
        _ -> triggerOnButtonPressedIfPointerOntoButton.onlyWhenReleased
      )
    Map(tuples.toSeq*)

  private def isPointerOntoButton(io: SwingIO): Boolean =
    val pointer = io.scenePointerPosition()
    val top = position.y + shapeHeight / 2
    val bottom = position.y - shapeHeight / 2
    val right = position.x + shapeWidth / 2
    val left = position.x - shapeWidth / 2

    pointer.x >= left &&
    pointer.x <= right &&
    pointer.y >= bottom &&
    pointer.y <= top

  private def triggerOnButtonPressedIfPointerOntoButton: Engine => Unit =
    engine =>
      isPointerOntoButton(engine.swingIO) match
        case true  => onButtonPressed(engine)
        case false => ()

  def onButtonPressed: Engine => Unit = _ => ()
