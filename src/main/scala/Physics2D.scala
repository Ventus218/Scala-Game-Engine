import Dimensions2D.*

object Physics2D:
    /** Gives the capability to detect an AABB collision to a Behaviour.
    *
    * @param w
    *   width of the collider, if omitted or less/equal than 0 it is equal to
    *   Positionable.width
    * @param h
    *   height of the collider, if omitted or less/equal than 0 it is equal to
    *   Positionable.height
    */
  trait Collider(private var width: Double, private var height: Double)
      extends Positionable with Scalable:
    require(width > 0)
    require(height > 0)

    def colliderWidth: Double = width * scaleX
    def colliderHeight: Double = height * scaleY

    def colliderWidth_=(w: Double): Unit = if w > 0 then width = w
    def colliderHeight_=(h: Double): Unit = if h > 0 then height = h

    /** Detect if this Behaviour collided with another Behaviour that extends
      * Collider using an AABB collision detection algorithm
      *
      * @param other
      * @return
      *   true if a collision is detected, false otherwise
      */
    def collides(other: Collider): Boolean =
      this.y <= other.y + other.height &&
        this.x <= other.x + other.width &&
        this.y + this.height >= other.y &&
        this.x + this.width >= other.x
