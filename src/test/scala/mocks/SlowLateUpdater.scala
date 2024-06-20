trait SlowLateUpdater(slowDownDurationMillis: Long) extends Behaviour:
  override def onLateUpdate: Engine => Unit = (engine) =>
    Thread.sleep(slowDownDurationMillis)
    super.onLateUpdate(engine)
