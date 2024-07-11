package scenes

import config.Config.*

import sge.core.*

object LevelTwo extends Scene:
  override def apply(): Iterable[Behaviour] = Level(this, LevelThree, (SCENE_WIDTH / 2 - STAIRS_WIDTH, 0)) ++ Seq(
    
  )
