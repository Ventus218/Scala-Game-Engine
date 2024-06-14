trait IO:
  def onFrameEnd: Engine => Unit = (_) => {}
