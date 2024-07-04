import Dimensions2D.*

object Physics2D:
  /** Gives colliders the methods to implement in order to detect the collisions
    * with other colliders
    */
  trait Collider extends Positionable:
    /** Detect if there was a collision with a Rectangle
      *
      * @param other
      * @return
      *   true if a detection is detected, false otherwise
      */
    def collides(other: RectCollider): Boolean

    /** Detect if there was a collision with a Circle
      *
      * @param other
      * @return
      *   true if a detection is detected, false otherwise
      */
    def collides(other: CircleCollider): Boolean

  /** Gives the capability to detect a collision to a Behaviour. Width and
    * Height are scaled based on ScaleX and ScaleY of the Dimensions2D.Scalable
    * trait. The shape of this collider is a Rectangle. The center of the
    * collider is based on X and Y of Positionable.
    *
    * @param width
    *   width of the collider, must be greater than zero otherwise throws an
    *   IllegalArgumentException
    * @param height
    *   height of the collider, must be greater than zero otherwise throws an
    *   IllegalArgumentException
    */
  trait RectCollider(private var width: Double, private var height: Double)
      extends Collider
      with Scalable[(Double, Double)]:
    require(width > 0)
    require(height > 0)

    def colliderWidth: Double = width * scale._1
    def colliderHeight: Double = height * scale._2

    def colliderWidth_=(w: Double): Unit = if w > 0 then width = w
    def colliderHeight_=(h: Double): Unit = if h > 0 then height = h

    private def right: Double = x + colliderWidth / 2
    private def bottom: Double = y + colliderHeight / 2
    private def left: Double = x - colliderWidth / 2
    private def top: Double = y - colliderHeight / 2

    override final def collides(other: RectCollider): Boolean =
      this.top <= other.bottom &&
        this.left <= other.right &&
        this.right >= other.left &&
        this.bottom >= other.top

    override final def collides(other: CircleCollider): Boolean =
      val cx = other.x
      val cy = other.y

      val dx =
        if cx < left then left - cx else if cx > right then cx - right else 0
      val dy =
        if cy < top then top - cy else if cy > bottom then cy - bottom else 0

      val distance = Math.sqrt(dx * dx + dy * dy)
      distance <= other.radius

  /** Gives the capability to detect a collision to a Behaviour. The shape of
    * the collider is a Circle. Radius is scaled based on scale of
    * Dimensions2D.SingleScalable trait. The center of the collider is based on
    * its X and Y.
    *
    * @param r
    *   radius of the collider, must be greater than zero otherwise throws an
    *   IllegalArgumentException
    */
  trait CircleCollider(private var r: Double)
      extends Collider
      with Scalable[Double]:
    require(r > 0)

    def radius: Double = scale * r
    def radius_=(radius: Double) = if radius > 0 then r = radius

    override final def collides(other: RectCollider): Boolean =
      other.collides(this)

    override final def collides(other: CircleCollider): Boolean =
      val dx = x - other.x
      val dy = y - other.y

      val distance = Math.sqrt(dx * dx + dy * dy)

      distance <= radius + other.radius

  /** Add velocity to a Positionable behaviour, in order to move it once the
    * onUpdate is called.
    *
    * @param _velocity
    *   value of X and Y to add to the position in order to move the behaviour.
    *   It must be not null or otherwise throws an IllegalArgumentException.
    */
  trait Velocity(private var _velocity: (Double, Double) = (0, 0)) extends Positionable:
    def velocity: (Double, Double) = _velocity
    def velocity_=(v: (Double, Double)) = _velocity = v

    override def onUpdate: Engine => Unit =
      engine =>
        super.onUpdate(engine)
        this.x = this.x + velocity._1 * engine.deltaTimeSeconds
        this.y = this.y + velocity._2 * engine.deltaTimeSeconds
