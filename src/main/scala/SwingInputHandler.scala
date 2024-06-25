import SwingIO.InputButton

object SwingInputHandler:

  type Handler = (InputButton) => Unit

  trait SwingInputHandler extends Behaviour:
    var inputHandlers: Map[InputButton, Handler]

    override def onEarlyUpdate: Engine => Unit = (engine) =>
      val io = engine.io.asInstanceOf[SwingIO]

      inputHandlers
        .filterKeys(io.inputButtonWasPressed(_))
        .foreachEntry((key, handler) => handler(key))

      super.onEarlyUpdate(engine)
