package model.behaviours.player

import sge.core.*
import model.behaviours.*
import enemies.*
import scenes.LoseGame

private object PlayerCollisions:
  def collidesWithEnemies(engine: Engine, player: Player, currentScene: Scene) =
    val enemies = engine.find[VisualRange]() ++ engine.find[Enemy]()
    enemies.collectFirst(collider =>
      if player.collides(collider)
      then
        player.lifes = player.lifes - 1
        updateLifes(engine, player.lifes)
        if player.lifes <= 0 then engine.loadScene(LoseGame)
        else engine.loadScene(currentScene)
    )

  def collidesWithStairs(engine: Engine, player: Player, nextScene: Scene) = 
    val stairs = engine.find[Stairs]()
    stairs.collectFirst(stair =>
      if (player.collides(stair)) then
        player.lifes = player.lifes + 1
        updateLifes(engine, player.lifes)
        engine.loadScene(nextScene)
    )

  private def updateLifes(engine: Engine, lifes: Int) =
    engine.storage.set("Lifes", lifes)