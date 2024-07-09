package entities

trait Health(var health: Int):
  require(health > 0)
  protected def onHit(): Unit
  protected def onDeath(): Unit
  
  def isDead: Boolean = health <= 0
  def hit(damage: Int): Unit =
    if !isDead then
      health -= damage
      if isDead then 
        health = 0
        onDeath() 
      else 
        onHit()
        
  def kill(): Unit =
    if !isDead then
      health = 0
      onDeath()