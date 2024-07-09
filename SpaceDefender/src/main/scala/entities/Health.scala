package entities

/** Basic trait for managing health on an entity
  */
trait Health(var health: Int):
  require(health > 0)
  protected def onHit(): Unit = ()
  protected def onDeath(): Unit = ()
  
  def isDead: Boolean = health <= 0

  /** Hit this entity and lose health
    * @param damage
    */
  def hit(damage: Int): Unit =
    if !isDead then
      health -= damage
      if isDead then 
        health = 0
        onDeath() 
      else 
        onHit()

  /** Kill the entity
    */
  def kill(): Unit =
    if !isDead then
      health = 0
      onDeath()