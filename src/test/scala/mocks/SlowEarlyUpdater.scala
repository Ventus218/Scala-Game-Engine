trait SlowEarlyUpdater(slowDownDurationMillis: Long) extends Behaviour:
  override def onEarlyUpdate: Engine => Unit = (engine) =>
    Thread.sleep(slowDownDurationMillis)
    super.onEarlyUpdate(engine)
