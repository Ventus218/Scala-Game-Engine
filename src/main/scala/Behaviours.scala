object Behaviours:
  /** Add an id to a Behaviour so that the Behaviour can be found by that id
    *
    * @param id
    *   id of the Behaviour
    */
  trait Identifiable(val id: String) extends Behaviour

  /** Add 2D position to a Behaviour
    *
    * @param x
    *   position of the Behaviour on the X axis
    * @param y
    *   position of the Behaviour on the Y axis
    */
  trait Positionable(var x: Double = 0, var y: Double = 0) extends Behaviour

  /** Gives to other behaviours a scale on X and Y of their dimension.
    *
    * @param x
    *   multiplier of the width, must be greater than 0.
    * @param y
    *   multiplier of the height, must be greater than 0.
    */
  trait Scalable(private var x: Double, private var y: Double)
      extends Behaviour:

    def scaleX: Double = if x > 0 then x else 1
    def scaleY: Double = if y > 0 then y else 1

    def scaleX_=(w: Double): Unit =
      if w > 0 then this.x = w

    def scaleY_=(h: Double): Unit =
      if h > 0 then this.y = h

  /** Gives the capability to detect an AABB collision to a Behaviour.
    *
    * @param w
    *   width of the collider, if omitted or less/equal than 0 it is equal to
    *   Positionable.width
    * @param h
    *   height of the collider, if omitted or less/equal than 0 it is equal to
    *   Positionable.height
    */
  trait Collider(private var w: Double = 0, private var h: Double = 0)
      extends Positionable:

    dimensionable: Scalable =>
    def colliderWidth: Double = if w <= 0 then scaleX else w
    def colliderHeight: Double = if h <= 0 then scaleY else h

    def colliderWidth_=(width: Double): Unit = if width > 0 then w = width
    def colliderHeight_=(height: Double): Unit = if height > 0 then h = height

    /** Detect if this Behaviour collided with another Behaviour that extends
      * Collider using an AABB collision detection algorithm
      *
      * @param other
      * @return
      *   true if a collision is detected, false otherwise
      */
    def collides(other: Collider): Boolean =
      this.y <= other.y + other.colliderHeight &&
        this.x <= other.x + other.colliderWidth &&
        this.y + this.colliderHeight >= other.y &&
        this.x + this.colliderWidth >= other.x

  /** A game object mixed with this behaviour will stop the engine after the
    * given amount of frame has been run
    *
    * @param nFramesToRun
    */
  trait NFrameStopper(val nFramesToRun: Int) extends Behaviour:
    require(nFramesToRun >= 0)

    private var frameCounter: Int = 0

    private def stopEngineIfNeeded(engine: Engine): Unit =
      if frameCounter > nFramesToRun then engine.stop()

    override def onStart: Engine => Unit = (engine) =>
      frameCounter += 1
      stopEngineIfNeeded(engine)
      super.onStart(engine)

    override def onUpdate: Engine => Unit = (engine) =>
      frameCounter += 1
      stopEngineIfNeeded(engine)
      super.onUpdate(engine)
