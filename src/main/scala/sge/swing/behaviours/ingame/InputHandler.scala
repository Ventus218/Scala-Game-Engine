package sge.swing.behaviours.ingame

import sge.core.*
import sge.swing.*
import input.InputButton
import Utils.*

object InputHandler:

  /** A Handler represents what should be done in response of an input event.
    *
    * When passing a simple function as a handler it will be fired on every
    * frame that the input is received, so just `f` is the same as
    * `(f.onlyWhenPressed and f.onlyWhenHeld)`
    */
  opaque type Handler = Iterable[SingleHandlerImpl]
  private case class SingleHandlerImpl(
      val handler: InputButton => Engine => Unit,
      val fireOnPressed: Boolean,
      val fireOnRelease: Boolean,
      val fireOnHold: Boolean
  )

  extension (h: Handler)
    /** Merges the current handler and another one into a signle Handler */
    infix def and(otherHandler: Handler): Handler =
      h ++ otherHandler

    /** Makes a handler fire only the in first frame the event happens */
    def onlyWhenPressed: Handler =
      h.map(h => SingleHandlerImpl(h.handler, true, false, false))

    /** Makes a handler fire only the in first frame the event stops happening
      */
    def onlyWhenReleased: Handler =
      h.map(h => SingleHandlerImpl(h.handler, false, true, false))

    /** Makes a handler fire on every frame after it has been held for at least
      * one
      */
    def onlyWhenHeld: Handler =
      h.map(h => SingleHandlerImpl(h.handler, false, false, true))

  given fullHandlerConversion
      : Conversion[InputButton => Engine => Unit, Handler] with
    def apply(f: InputButton => Engine => Unit): Handler =
      Seq(SingleHandlerImpl(f, true, false, true))

  given partialHandlerConversion: Conversion[Engine => Unit, Handler] with
    def apply(f: Engine => Unit): Handler =
      Seq(SingleHandlerImpl((_) => f, true, false, true))

  given unitHandlerConversion: Conversion[Unit, Handler] with
    def apply(f: Unit): Handler =
      Seq(SingleHandlerImpl((_) => (_) => f, true, false, true))

  /** A behaviour which enables to specify some event handlers to fire when
    * specific inputs are received.
    *
    * All the handlers that need to be fired are executed automatically during
    * the EarlyUpdate phase of the game loop.
    */
  trait InputHandler extends Behaviour:
    /** A mapping between input buttons and their handlers.
      */
    var inputHandlers: Map[InputButton, Handler]

    /** Serves the purpose of recording inputs that were received in the last
      * frame
      */
    private var lastFrameReceivedInputs: Set[InputButton] = Set()

    override def onEarlyUpdate: Engine => Unit = (engine) =>
      val io = engine.swingIO

      val receivedInputs = inputHandlers
        .filterKeys(io.inputButtonWasPressed(_))

      receivedInputs.toSeq
        .flatMap((inputButton, handlers) => handlers.map((inputButton, _)))
        .foreach((inputButton, h) =>
          lastFrameReceivedInputs(inputButton) match
            case false if h.fireOnPressed => h.handler(inputButton)(engine)
            case true if h.fireOnHold     => h.handler(inputButton)(engine)
            case _                        => ()
        )

      val releasedInputs = lastFrameReceivedInputs -- receivedInputs.keys.toSet

      releasedInputs.foreach(inputButton =>
        inputHandlers(inputButton).foreach(h =>
          if h.fireOnRelease then h.handler(inputButton)(engine)
        )
      )

      lastFrameReceivedInputs = receivedInputs.keys.toSet

      super.onEarlyUpdate(engine)
