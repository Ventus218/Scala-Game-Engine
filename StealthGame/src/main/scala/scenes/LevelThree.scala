package scenes

import config.Config.*
import sge.core.*

object LevelThree extends Scene:
  override def apply(): Iterable[Behaviour] = Level(this, WinLevel, (0, -SCENE_HEIGHT / 2 + STAIRS_HEIGHT)) ++ Seq(
    
  )
