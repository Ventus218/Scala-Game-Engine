import SwingIO.InputButton

object SwingInputHandler:

  opaque type Handler = HandlerImpl
  private case class HandlerImpl(
      val handler: (InputButton) => Unit,
      val shouldFireJustOnceIfHold: Boolean,
      val nextHandler: Option[Handler]
  )

  extension (ih: Handler)
    infix def and(otherHandler: Handler): Handler = ih match
      case HandlerImpl(h, fireOnce, None) =>
        HandlerImpl(h, fireOnce, Some(otherHandler))
      case HandlerImpl(h, fireOnce, Some(other)) =>
        HandlerImpl(h, fireOnce, Some(other and otherHandler))

    def fireJustOnceIfHold: Handler =
      HandlerImpl(ih.handler, true, ih.nextHandler)

    def fire(inputButton: InputButton): Unit =
      ih.handler(inputButton)
      ih.nextHandler.foreach(_.fire(inputButton))

  given Conversion[(InputButton) => Unit, Handler] with
    def apply(x: InputButton => Unit): Handler =
      HandlerImpl(x, false, None)

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
        .foreachEntry((key, handler) =>
          if !handler.shouldFireJustOnceIfHold || !lastFrameReceivedInputs.contains(key)
          then handler.fire(key)
        )

      lastFrameReceivedInputs = receivedInputs.keys.toSet

      super.onEarlyUpdate(engine)
