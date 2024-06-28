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
      with Scalable:
    require(width > 0)
    require(height > 0)

    def colliderWidth: Double = width * scaleX
    def colliderHeight: Double = height * scaleY

    def colliderWidth_=(w: Double): Unit = if w > 0 then width = w
    def colliderHeight_=(h: Double): Unit = if h > 0 then height = h

    private def right: Double = x + colliderWidth / 2
    private def bottom: Double = y + colliderHeight / 2
    private def left: Double = x - colliderWidth / 2
    private def top: Double = y - colliderHeight / 2

    override def collides(other: RectCollider): Boolean =
      this.top <= other.bottom &&
        this.left <= other.right &&
        this.right >= other.left &&
        this.bottom >= other.top

    override def collides(other: CircleCollider): Boolean =
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
      with SingleScalable:
    require(r > 0)

    def radius: Double = scale * r
    def radius_=(radius: Double) = if radius > 0 then r = radius

    override def collides(other: RectCollider): Boolean = other.collides(this)

    override def collides(other: CircleCollider): Boolean =
      val dx = x - other.x
      val dy = y - other.y

      val distance = Math.sqrt(dx * dx + dy * dy)

      distance <= radius + other.radius