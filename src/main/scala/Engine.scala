import scala.annotation.targetName

trait Engine:
  val io: IO
  val storage: Storage
  def loadScene(scene: Scene): Unit
  def enable(gameObject: Enableable): Unit
  def disable(gameObject: Enableable): Unit
  def create(gameObject: Behaviour): Unit
  def destroy(gameObject: Behaviour): Unit
  def run(): Unit
  def stop(): Unit
  def deltaTimeNanos: Long

  import scala.reflect.TypeTest

  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[B]

  def find[B <: Identifiable](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[B]

class EngineImpl(val io: IO, val storage: Storage, private var scene: Scene)
    extends Engine:

  override def create(gameObject: Behaviour): Unit = ???
  override def destroy(gameObject: Behaviour): Unit = ???

  private var gameObjects: Seq[Behaviour] = Seq()

  private var gameObjectsToDisable: Seq[Enableable] = Seq()
  private var gameObjectsToEnable: Seq[Enableable] = Seq()

  private var sceneToLoad: Option[Scene] = Option(scene)

  def loadScene(scene: Scene): Unit = sceneToLoad = Option(scene)

  def enable(gameObject: Enableable): Unit =
    gameObjectsToEnable = gameObjectsToEnable.appended(gameObject)
  def disable(gameObject: Enableable): Unit =
    gameObjectsToDisable = gameObjectsToDisable.appended(gameObject)

  private var shouldStop = false;
  def stop(): Unit = shouldStop = true;

  private var _deltaTimeNanos: Long = 0
  def deltaTimeNanos: Long = _deltaTimeNanos
  private def deltaTimeNanos_=(value: Long) = _deltaTimeNanos = value
  def run(): Unit =
    val context = Context(this)
    while !shouldStop do
      deltaTimeNanos = 0
      if sceneToLoad.isDefined then applyScene(sceneToLoad.get)

      gameObjects
        .foreach(_.onInit(context))

      enabledBehaviours().foreach(_.onEnabled(context))
      enabledBehaviours().foreach(_.onStart(context))

      // Game loop
      while (sceneToLoad.isEmpty && !shouldStop) do
        val start = System.nanoTime()
        gameObjectsToEnable
          .foreach(b =>
            b.enabled = true
            b.onEnabled(context)
          )
        gameObjectsToEnable = Seq()
        gameObjectsToDisable
          .foreach(b =>
            b.enabled = false
            b.onDisabled(context)
          )
        gameObjectsToDisable = Seq()
        enabledBehaviours().foreach(_.onEarlyUpdate(context))
        enabledBehaviours().foreach(_.onUpdate(context))
        enabledBehaviours().foreach(_.onLateUpdate(context))

        io.onFrameEnd(this)

        val end = System.nanoTime()
        deltaTimeNanos = end - start

      gameObjects.foreach(_.onDeinit(context))

  private def enabledBehaviours(): Seq[Enableable] =
    find[Enableable]().filter(_.enabled).toSeq

  private def applyScene(scene: Scene) =
    sceneToLoad = Option.empty
    gameObjects = scene.gameObjects().toSeq

  import scala.reflect.ClassTag
  import scala.reflect.TypeTest

  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[B] =
    gameObjects
      .filter(_ match
        case tt(_) => true
        case _     => false
      )
      .map(_.asInstanceOf[B])

  def find[B <: Identifiable](using tt: TypeTest[Behaviour, B])(
      id: String
  ): Option[B] =
    find[B]().find(_.id == id)

object EngineImpl:
  def apply(io: IO, storage: Storage, scene: Scene): EngineImpl =
    new EngineImpl(io, storage, scene)

@main def main(): Unit =
  val io = ConsoleIO()
  val scene = new Scene {
    override val gameObjects: () => Iterable[Behaviour] =
      () =>
        Seq(
          Palla(rimbalzo = true, id = "palla1"),
          new Behaviour with ConsoleIORendererB with Identifiable("a")
        )
  }
