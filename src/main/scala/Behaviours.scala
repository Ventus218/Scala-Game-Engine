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

  /** Add 2D dimension to a Behaviour. Does not work with negative values.
    *
    * @param w
    *   width of the Behaviour. If w < 0 when created, width will be 0
    * @param h
    *   height of the Behaviour. If h < 0 when created, height will be 0
    */
  trait Dimensionable(private var w: Double, private var h: Double)
      extends Behaviour:

    def width: Double = if w >= 0 then w else 0
    def height: Double = if h >= 0 then h else 0

    def width_=(w: Double): Unit =
      if w >= 0 then this.w = w

    def height_=(h: Double): Unit =
      if h >= 0 then this.h = h

  /** Gives the capability to detect an AABB collision to a Behaviour.
    *
    * @param w width of the collider, if omitted or less/equal than 0 it is equal to Positionable.width
    * @param h height of the collider, if omitted or less/equal than 0 it is equal to Positionable.height
    */
  trait Collider(private var w: Double = 0, private var h: Double = 0)
      extends Positionable:

    dimensionable: Dimensionable =>
      def colliderWidth: Double = if w <= 0 then width else w
      def colliderHeight: Double = if h <= 0 then height else h

    def colliderWidth_=(width: Double): Unit = if width > 0 then w = width
    def colliderHeight_=(height: Double): Unit = if height > 0 then h = height

    /** Detect if this Behaviour collided with another Behaviour that extends Collider 
      * using an AABB collision detection algorithm
      *
      * @param other
      * @return true if a collision is detected, false otherwise
      */
    def collides(other: Collider): Boolean =
        this.y <= other.y + other.colliderHeight &&
        this.x <= other.x + other.colliderWidth &&
        this.y + this.colliderHeight >= other.y &&
        this.x + this.colliderWidth >= other.x