import SwingIO.InputButton

object SwingInputHandler:

  type Handler = (InputButton) => Unit

  /** A behaviour which enables to specify some event handlers to fire when
    * specific inputs are received.
    *
    * All the handlers that need to be fired are executed automatically during
    * the LateUpdate phase of the game loop.
    */
  trait SwingInputHandler extends Behaviour:
    /** A mapping between input buttons and their handlers.
      */
    var inputHandlers: Map[InputButton, Handler]

    override def onEarlyUpdate: Engine => Unit = (engine) =>
      val io = engine.io.asInstanceOf[SwingIO]

      inputHandlers
        .filterKeys(io.inputButtonWasPressed(_))
        .foreachEntry((key, handler) => handler(key))

      super.onEarlyUpdate(engine)
