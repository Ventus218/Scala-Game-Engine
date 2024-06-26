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
