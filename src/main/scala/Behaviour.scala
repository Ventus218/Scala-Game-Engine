trait Behaviour:
  // The order of the methods below reflects the order on which the engine will call them.

  /** Called only once when the object is instatiated.
    */
  def onInit: Engine => Unit = (_) => {}

  /** Called after the object is instantiated as enabled. Called every time the
    * object goes from disabled to enabled.
    */
  def onEnabled: Engine => Unit = (_) => {}

  /** Called every time the object goes from enabled to disabled.
    */
  def onDisabled: Engine => Unit = (_) => {}

  /** Called only once when the object is enabled for the first time.
    */
  def onStart: Engine => Unit = (_) => {}

  /** Called once every frame before onUpdate.
    *
    * Is called only if the object is enabled.
    */
  def onEarlyUpdate: Engine => Unit = (_) => {}

  /** Called once every frame.
    *
    * Is called only if the object is enabled.
    */
  def onUpdate: Engine => Unit = (_) => {}

  /** Called once every frame after onUpdate.
    *
    * Is called only if the object is enabled.
    */
  def onLateUpdate: Engine => Unit = (_) => {}

  /** Called only once before the engine destroys the object.
    */
  def onDeinit: Engine => Unit = (_) => {}