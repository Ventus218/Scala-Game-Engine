trait Engine:
  var gameObjects: Iterable[GameObject[?]]
  var scene: Scene 
  val io: IO

  def loadScene(scene: Scene): Unit

trait IO