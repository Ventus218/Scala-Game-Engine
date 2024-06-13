import scala.annotation.targetName

trait Engine:
  val io: IO
  def loadScene(scene: Scene): Unit
  def enable(gameObject: GameObject[?]): Unit
  def disable(gameObject: GameObject[?]): Unit
  def create(gameObject: GameObject[?]): Unit
  def destroy(gameObject: GameObject[?]): Unit
  def run(): Unit
  def stop(): Unit
  def deltaTimeNanos: Long

  import scala.reflect.TypeTest

  @targetName("find_object")
  def find[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(): Iterable[G]

  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[GameObject[B]]

  @targetName("find_object_by_id")
  def findById[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(id: String): Option[G]

  def findById[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[GameObject[B]]

class EngineImpl(val io: IO, private var scene: Scene) extends Engine:

  override def destroy(gameObject: GameObject[?]): Unit = ???

  override def create(gameObject: GameObject[?]): Unit = ???

  private var gameObjects: Seq[GameObject[?]] = Seq()

  private var gameObjectsToDisable: Seq[GameObject[?]] = Seq()
  private var gameObjectsToEnable: Seq[GameObject[?]] = Seq()

  private var sceneToLoad: Option[Scene] = Option(scene)

  def loadScene(scene: Scene): Unit = sceneToLoad = Option(scene)

  def enable(gameObject: GameObject[?]): Unit =
    gameObjectsToEnable = gameObjectsToEnable.appended(gameObject)
  def disable(gameObject: GameObject[?]): Unit =
    gameObjectsToDisable = gameObjectsToDisable.appended(gameObject)

  extension (gameObjects: Seq[GameObject[?]])
    def toContexts(): Seq[Context] = gameObjects.map(Context(this, _))

  private var shouldStop = false;
  def stop(): Unit = shouldStop = true;

  private var _deltaTimeNanos: Long = 0
  def deltaTimeNanos: Long = _deltaTimeNanos
  private def deltaTimeNanos_=(value: Long) = _deltaTimeNanos = value

  def run(): Unit =
    while !shouldStop do
      deltaTimeNanos = 0
      if sceneToLoad.isDefined then applyScene(sceneToLoad.get)

      gameObjects
        .toContexts()
        .foreach(context => context.gameObject.behaviour.onInit(context))

      enabledContexts().foreach(context =>
        context.gameObject.behaviour.onEnabled(context)
      )
      enabledContexts().foreach(context =>
        context.gameObject.behaviour.onStart(context)
      )

      // Game loop
      while (sceneToLoad.isEmpty && !shouldStop) do
        val start = System.nanoTime()
        gameObjectsToEnable
          .toContexts()
          .foreach(context =>
            context.gameObject.enabled = true
            context.gameObject.behaviour.onEnabled(context)
          )
        gameObjectsToEnable = Seq()
        gameObjectsToDisable
          .toContexts()
          .foreach(context =>
            context.gameObject.enabled = false
            context.gameObject.behaviour.onDisabled(context)
          )
        gameObjectsToDisable = Seq()
        enabledContexts().foreach(context =>
          context.gameObject.behaviour.onEarlyUpdate(context)
        )
        enabledContexts().foreach(context =>
          context.gameObject.behaviour.onUpdate(context)
        )
        enabledContexts().foreach(context =>
          context.gameObject.behaviour.onLateUpdate(context)
        )
        val end = System.nanoTime()
        deltaTimeNanos = end - start

      gameObjects
        .toContexts()
        .foreach(context => context.gameObject.behaviour.onDeinit(context))

  private def enabledGameObjects(): Seq[GameObject[?]] =
    gameObjects.filter(_.enabled)

  private def enabledContexts(): Seq[Context] =
    gameObjects
      .map(go => Context(this, go))
      .filter(_.gameObject.enabled)

  case class BehaviourWithContext(
      val behaviour: Behaviour,
      val context: Context
  )

  private def applyScene(scene: Scene) =
    sceneToLoad = Option.empty
    gameObjects = scene.objects().toSeq

  import scala.reflect.ClassTag
  import scala.reflect.TypeTest

  @targetName("find_object")
  def find[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(): Iterable[G] =
    gameObjects
      .filter(_ match
        case tt(_) => true
        case _     => false
      )
      .map(_.asInstanceOf[G])

  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[GameObject[B]] =
    gameObjects
      .filter(_.behaviour match
        case tt(_) => true
        case _     => false
      )
      .map(_.asInstanceOf[GameObject[B]])

  @targetName("find_object_by_id")
  def findById[G <: GameObject[?]](using
      tt: TypeTest[GameObject[?], G]
  )(id: String): Option[G] =
    find[G]().find(_.id == Some(id))

  def findById[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[GameObject[B]] =
    find[B]().find(_.id == Some(id))

object EngineImpl:
  def apply(io: IO, scene: Scene): EngineImpl = new EngineImpl(io, scene)

@main def main(): Unit =
  val io = ConsoleIO()
  val scene = new Scene {
    override val objects: () => Iterable[GameObject[?]] =
      () =>
        Seq(
          PallaGameObject(r = true, enabled = true),
          new GameObject {
            val id: Option[String] = Option("sasso")
            var enabled: Boolean = true
            val behaviour: RendererB = new Behaviour with ConsoleIORendererB
          }
        )
  }

  val a = EngineImpl(io, scene).findById[TranformB]("")
