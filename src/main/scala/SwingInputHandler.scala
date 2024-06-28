import SwingIO.InputButton

object SwingInputHandler:

  /** A Handler represents what should be done in response of an input event */
  opaque type Handler = Iterable[SingleHandlerImpl]
  private case class SingleHandlerImpl(
      val handler: (InputButton) => Unit,
      val shouldFireJustOnceIfHold: Boolean
  )

  extension (h: Handler)
    /** Merges the current handler and another one into a signle Handler */
    infix def and(otherHandler: Handler): Handler =
      h ++ otherHandler

    /** Makes a handler fire only once for each sequence of emitted events */
    def fireJustOnceIfHeld: Handler =
      h.map(h => SingleHandlerImpl(h.handler, true))

  given Conversion[(InputButton) => Unit, Handler] with
    def apply(f: InputButton => Unit): Handler =
      Seq(SingleHandlerImpl(f, false))

  /** A behaviour which enables to specify some event handlers to fire when
    * specific inputs are received.
    *
    * All the handlers that need to be fired are executed automatically during
    * the EarlyUpdate phase of the game loop.
    */
  trait SwingInputHandler extends Behaviour:
    /** A mapping between input buttons and their handlers.
      */
    var inputHandlers: Map[InputButton, Handler]

    /** Serves the purpose of recording inputs that were received in the last
      * frame
      */
    private var lastFrameReceivedInputs: Set[InputButton] = Set()

    override def onEarlyUpdate: Engine => Unit = (engine) =>
      val io = engine.io.asInstanceOf[SwingIO]

      val receivedInputs = inputHandlers
        .filterKeys(io.inputButtonWasPressed(_))

      receivedInputs
        .foreachEntry((key, handlers) =>
          handlers.foreach(h =>
            if !h.shouldFireJustOnceIfHold || !lastFrameReceivedInputs
                .contains(key)
            then h.handler(key)
          )
        )

      lastFrameReceivedInputs = receivedInputs.keys.toSet

      super.onEarlyUpdate(engine)
