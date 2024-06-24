import scala.annotation.targetName
import Behaviours.*
import BehaviourUtils.*
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
  def run(initialScene: Scene): Unit

  /** Tells to the engine to stop the game loop */
  def stop(): Unit

  /** Returns the time passed between the previous frame and the current frame
    * in nanoseconds
    */
  def deltaTimeNanos: Long

  import scala.reflect.TypeTest

  /** Retrieves all the objects having behaviour `B`
    *
    * @param tt
    * @return
    */
  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[B]

  /** Retrieves the first found Identifiable object having behaviour `B` and
    * matching identifier
    *
    * @param tt
    * @param id
    * @return
    */
  def find[B <: Identifiable](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[B]

object Engine:
  // gameObjects is used just to test the Engine until other Interfaces are implemented
  private class EngineImpl(override val io: IO, override val storage: Storage)
      extends Engine:

    private var gameObjects: Iterable[Behaviour] = Seq()
    private var sceneToChange: Option[Scene] = Option.empty

    private var gameObjectsToAdd: Seq[Behaviour] = Seq()
    private var gameObjectsToRemove: Seq[Behaviour] = Seq()

    override def loadScene(scene: Scene): Unit =
      sceneToChange = Option(scene)

    override def enable(gameObject: Behaviour): Unit = ???

    override def disable(gameObject: Behaviour): Unit = ???

    override def create(gameObject: Behaviour): Unit =
      if !gameObjects.exists(_ eq gameObject) then
        gameObjectsToAdd = gameObjectsToAdd :+ gameObject

    override def find[B <: Identifiable](using tt: TypeTest[Behaviour, B])(
        id: String
    ): Option[B] =
      find[B]().find(_.id == id)

    override def find[B <: Behaviour](using
        tt: TypeTest[Behaviour, B]
    )(): Iterable[B] =
      gameObjects.filter[B]()

    private var _deltaTimeNanos: Long = 0

    def deltaTimeNanos: Long = _deltaTimeNanos
    private def deltaTimeNanos_=(dt: Long) = this._deltaTimeNanos = dt

    override def destroy(gameObject: Behaviour): Unit =
      if gameObjects.exists(_ eq gameObject) then
        gameObjectsToRemove = gameObjectsToRemove :+ gameObject

    private def computeEvent(event: Behaviour => Unit, onlyEnabled: Boolean = true): Unit =
      gameObjects.filter(_.enabled || !onlyEnabled).foreach(event)
      gameObjects = gameObjects ++ gameObjectsToAdd
      gameObjects = gameObjects.filterNot(gameObjectsToRemove.contains)
      gameObjectsToAdd = Seq()
      gameObjectsToRemove = Seq()

    private var shouldStop = false

    override def run(initialScene: Scene): Unit =
      shouldStop = false
      deltaTimeNanos = 0
      loadScene(initialScene)

      while !shouldStop do
        gameObjects = sceneToChange.get()
        sceneToChange = Option.empty

        computeEvent(_.onInit(this), onlyEnabled = false)

        computeEvent(_.onEnabled(this))

        computeEvent(_.onStart(this))

        while !shouldStop && sceneToChange.isEmpty do
          val start = System.nanoTime()

          computeEvent(_.onEarlyUpdate(this))

          computeEvent(_.onUpdate(this))

          computeEvent(_.onLateUpdate(this))

          deltaTimeNanos = System.nanoTime() - start

        computeEvent(_.onDeinit(this), onlyEnabled = false)

    override def stop(): Unit = shouldStop = true

  /** Creates the engine.
    *
    * @param io
    *   The IO of the engine, used to render the view to the monitor and getting
    *   inputs by the user
    * @param storage
    *   The Storage that the engine will use to store values between scenes
    */
  def apply(io: IO, storage: Storage): Engine =
    new EngineImpl(io = io, storage = storage)
