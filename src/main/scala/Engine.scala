trait IO

class Engine(val io: IO, private var scene: Scene):
  private var gameObjects: Seq[GameObject[?]] = Seq()

  private var gameObjectsToDisable: Seq[GameObject[?]] = Seq()
  private var gameObjectsToEnable: Seq[GameObject[?]] = Seq()

  private var sceneToLoad: Option[Scene] = Option(scene)

  def loadScene(scene: Scene): Unit = sceneToLoad = Option(scene)

  def enable(gameObject: GameObject[?]): Unit = ???
  def disable(gameObject: GameObject[?]): Unit = ???

  private var shouldStop = false;
  def stop(): Unit = shouldStop = true;

  def run(): Unit =
    while !shouldStop do
      if sceneToLoad.isDefined then applyScene(sceneToLoad.get)

      gameObjects.foreach(_.behaviour.onInit())

      enabledBehaviours().foreach(_.onEnabled())
      enabledBehaviours().foreach(_.onStart())

      // Game loop
      while (sceneToLoad.isEmpty && !shouldStop) do
        gameObjectsToEnable.foreach(o =>
          o.enabled = true
          o.behaviour.onEnabled()
        )
        gameObjectsToEnable = Seq()
        gameObjectsToDisable.foreach(o =>
          o.enabled = false
          o.behaviour.onDisabled()
        )
        gameObjectsToDisable = Seq()
        enabledBehaviours().foreach(_.onEarlyUpdate())
        enabledBehaviours().foreach(_.onUpdate())
        enabledBehaviours().foreach(_.onLateUpdate())

      gameObjects.foreach(_.behaviour.onDeinit())

  private def enabledGameObjects(): Seq[GameObject[?]] =
    gameObjects.filter(_.enabled)

  private def enabledBehaviours(): Seq[Behaviour] =
    gameObjects.filter(_.enabled).map(_.behaviour)

  private def applyScene(scene: Scene) =
    sceneToLoad = Option.empty
    gameObjects = scene.objects().toSeq

object Engine:
  def apply(io: IO, scene: Scene): Engine = new Engine(io, scene)

@main def main(): Unit =
  val io = new IO {}
  val scene = new Scene {
    override val objects: () => Iterable[GameObject[?]] = () => Seq()
  }

  Engine(io, scene).run()
