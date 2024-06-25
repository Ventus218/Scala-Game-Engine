import SwingIO.Key

object SwingInputHandler:

  type Handler = (Key) => Unit

  trait SwingInputHandler extends Behaviour:
    var keyHandlers: Map[Key, Handler]

    override def onEarlyUpdate: Engine => Unit = (engine) =>
      val io = engine.io.asInstanceOf[SwingIO]

      keyHandlers
        .filterKeys(io.keyWasPressed(_))
        .foreachEntry((key, handler) => handler(key))

      super.onEarlyUpdate(engine)
