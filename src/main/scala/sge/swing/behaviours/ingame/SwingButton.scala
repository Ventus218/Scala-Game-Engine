package sge.swing.behaviours.ingame

import sge.core.*
import metrics.Vector.*
import behaviours.dimension2d.*
import sge.swing.*
import Utils.*
import input.InputButton
import output.*
import Text.*
import InputButton.*
import SwingInputHandler.{*, given}
import java.awt.{Graphics2D, Color}

/** A swing button with text.
  *
  * @param _inputButtonTriggers
  *   defines the input buttons (like mouse or spacebar) that can trigger the
  *   button
  */
trait SwingButton(
    _buttonText: String = "",
    _textSize: Double = 5,
    _textColor: Color = Color.black,
    _textFont: FontName = "Arial",
    _textStyle: TextStyle = TextStyle.Plain,
    _textOffset: Vector = (0, 1),
    private var _inputButtonTriggers: Set[InputButton] = Set(MouseButton1)
) extends Behaviour
    with SwingRectRenderer
    with SwingInputHandler:

  private val textRenderer: SwingTextRenderer = new Behaviour
    with SwingTextRenderer(
      _buttonText,
      _textSize,
      _textColor,
      _textFont,
      _textStyle,
      _textOffset,
      priority = renderingPriority + 1
    )
    with Positionable(position)

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

  // Ensure that changes to button text elements reflect on textRenderer
  export textRenderer.{
    textContent => buttonText,
    textContent_= => buttonText_=,
    textSize,
    textSize_=,
    textColor,
    textColor_=,
    textFont,
    textFont_=,
    textStyle,
    textStyle_=
  }
  def textOffset = textRenderer.renderOffset
  def textOffset_=(newValue: Vector) = textRenderer.renderOffset = newValue

  // Just relaying every gameloop event call to textRenderer and making it follow the button on Update
  override def onInit: Engine => Unit = engine =>
    textRenderer.onInit(engine)
    super.onInit(engine)
  override def onStart: Engine => Unit = engine =>
    textRenderer.onStart(engine)
    super.onStart(engine)
  override def onEarlyUpdate: Engine => Unit = engine =>
    textRenderer.onEarlyUpdate(engine)
    super.onEarlyUpdate(engine)
  override def onUpdate: Engine => Unit = engine =>
    textRenderer.onUpdate(engine)
    textRenderer.position = position
    super.onUpdate(engine)
  override def onLateUpdate: Engine => Unit = engine =>
    textRenderer.onLateUpdate(engine)
    super.onLateUpdate(engine)
  override def onDeinit: Engine => Unit = engine =>
    textRenderer.onDeinit(engine)
    super.onDeinit(engine)
  override def onEnabled: Engine => Unit = engine =>
    textRenderer.onEnabled(engine)
    super.onEnabled(engine)
  override def onDisabled: Engine => Unit = engine =>
    textRenderer.onDisabled(engine)
    super.onDisabled(engine)
