import scala.annotation.targetName
import Behaviours.*
import scala.reflect.TypeTest

/** Takes care of starting and stopping the game loop, enable/disable Behaviours
  * of the loaded Scene, create/destroy Behaviours of the loaded Scene, find
  * some Behaviours of the loaded Scene and gives the delta time in nanoseconds.
  */
trait Engine:
  /** The IO of the engine, used to render the view to the monitor and getting
    * inputs by the user
    */
  val io: IO

  /** The Storage that the engine will use to store values between scenes */
  val storage: Storage

  def loadScene(scene: Scene): Unit
  def enable(gameObject: Behaviour): Unit
  def disable(gameObject: Behaviour): Unit
  def create(gameObject: Behaviour): Unit
  def destroy(gameObject: Behaviour): Unit

  /** Starts the game loop of the engine */
  def run(): Unit

  /** Tells to the engine to stop the game loop */
  def stop(): Unit

  /** Returns the time passed between the previous frame and the current frame in nanoseconds
    */
  def deltaTimeNanos: Long

  import scala.reflect.TypeTest

  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[B]

  def find[B <: Identifiable](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[B]

object Engine:
  // gameObjects and numSteps are used just to test the Engine until other Interfaces are implemented
  private class EngineImpl(
      override val io: IO,
      override val storage: Storage,
      gameObjects: Iterable[Behaviour]
  ) extends Engine:

    override def loadScene(scene: Scene): Unit = ???

    override def enable(gameObject: Behaviour): Unit = ???

    override def disable(gameObject: Behaviour): Unit = ???

    override def create(gameObject: Behaviour): Unit = ???

    override def find[B <: Identifiable](using tt: TypeTest[Behaviour, B])(
        id: String
    ): Option[B] = ???

    override def find[B <: Behaviour](using
        tt: TypeTest[Behaviour, B]
    )(): Iterable[B] = ???

    private var _deltaTimeNanos: Long = 0

    def deltaTimeNanos: Long = _deltaTimeNanos
    private def deltaTimeNanos_=(dt: Long) = this._deltaTimeNanos = dt

    override def destroy(gameObject: Behaviour): Unit = ???

    private def enabledGameObjects =
      gameObjects.filter(_.enabled)

    private var shouldStop = false

    override def run(): Unit =
      shouldStop = false
      deltaTimeNanos = 0
      gameObjects.foreach(_.onInit(this))

      enabledGameObjects.foreach(_.onEnabled(this))

      enabledGameObjects.foreach(_.onStart(this))

      while !shouldStop do
        val start = System.nanoTime()

        enabledGameObjects.foreach(_.onEarlyUpdate(this))

        enabledGameObjects.foreach(_.onUpdate(this))

        enabledGameObjects.foreach(_.onLateUpdate(this))

        deltaTimeNanos = System.nanoTime() - start

      gameObjects.foreach(_.onDeinit(this))

    override def stop(): Unit = shouldStop = true

  /** Creates the engine.
    *
    * @param io
    *   The IO of the engine, used to render the view to the monitor and getting
    *   inputs by the user
    * @param storage
    *   The Storage that the engine will use to store values between scenes
    */
  def apply(
      io: IO,
      storage: Storage,
      gameObjects: Iterable[Behaviour]
  ): Engine =
    new EngineImpl(
      io = io,
      storage = storage,
      gameObjects = gameObjects
    )
