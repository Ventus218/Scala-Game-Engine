trait Behaviour:
  // The order of the methods below reflects the order on which the engine will call them.

  /** Called only once when the object is instatiated.
    */
  def onInit: Context => Unit = (context) => {}

  /** Called after the object is instantiated as enabled. Called every time the
    * object goes from disabled to enabled.
    */
  def onEnabled: Context => Unit = (context) => {}

  /** Called every time the object goes from enabled to disabled.
    */
  def onDisabled: Context => Unit = (context) => {}

  /** Called only once when the object is enabled for the first time.
    */
  def onStart: Context => Unit = (context) => {}

  /** Called once every frame before onUpdate.
    *
    * Is called only if the object is enabled.
    */
  def onEarlyUpdate: Context => Unit = (context) => {}

  /** Called once every frame.
    *
    * Is called only if the object is enabled.
    */
  def onUpdate: Context => Unit = (context) => {}

  /** Called once every frame after onUpdate.
    *
    * Is called only if the object is enabled.
    */
  def onLateUpdate: Context => Unit = (context) => {}

  /** Called only once before the engine destroys the object.
    */
  def onDeinit: Context => Unit = (context) => {}

trait PositionB extends Behaviour:
  val x: Int = 0
  val y: Int = 0