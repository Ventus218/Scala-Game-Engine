import SwingRenderers.SwingRectRenderer
import SwingIO.*
import InputButton.*
import SwingInputHandler.{*, given}
import Dimensions2D.Vector.*

trait SwingButton(
    var buttonText: String = "",
    private var _inputButtonTriggers: Set[InputButton] = Set(MouseButton1)
) extends Behaviour
    with SwingRectRenderer
    with SwingInputHandler:

  var inputHandlers: Map[InputButton, Handler] = makeInputHandlers()

  def inputButtonTriggers: Set[InputButton] = _inputButtonTriggers
  def inputButtonTriggers_=(newValue: Set[InputButton]) =
    _inputButtonTriggers = newValue
    inputHandlers = makeInputHandlers()
    isPressed = makeIsPressedMap(Option(isPressed))

  private def makeInputHandlers(): Map[InputButton, Handler] =
    /* Apparently needs to be explicit about the type to let the conversion work */
    val tuples: Set[(InputButton, Handler)] =
      inputButtonTriggers.map(
        _ -> (buttonReleased.onlyWhenReleased and buttonPressed.onlyWhenPressed)
      )
    Map(tuples.toSeq*)

  var isPressed: Map[InputButton, Boolean] = makeIsPressedMap()

  private def makeIsPressedMap(
      currentIsPressedMap: Option[Map[InputButton, Boolean]] = None
  ): Map[InputButton, Boolean] =
    inputHandlers.keySet
      .map(key =>
        currentIsPressedMap match
          case Some(currentMap) => (key, currentMap.getOrElse(key, false))
          case None             => (key, false)
      )
      .toMap

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

  private def buttonPressed: InputButton => Engine => Unit =
    inputButton =>
      engine =>
        isPointerOntoButton(engine.swingIO) match
          case true  => isPressed = isPressed.updated(inputButton, true)
          case false => ()

  private def buttonReleased: InputButton => Engine => Unit =
    inputButton =>
      engine =>
        isPointerOntoButton(engine.swingIO) match
          case true if isPressed(inputButton) => onButtonPressed(engine)
          case _                              => ()
        isPressed = isPressed.updated(inputButton, false)

  def onButtonPressed: Engine => Unit = _ => ()
