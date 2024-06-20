trait SlowUpdater(slowDownDurationMillis: Long) extends Behaviour:
  override def onUpdate: Engine => Unit = (engine) =>
    Thread.sleep(slowDownDurationMillis)
    super.onUpdate(engine)
