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
    private var sceneToLoad: Option[Scene] = Option.empty

    private var gameObjectsToAdd: Seq[Behaviour] = Seq()
    private var gameObjectsToRemove: Seq[Behaviour] = Seq()

    override def loadScene(scene: Scene): Unit =
      sceneToLoad = Option(scene)

    override def enable(gameObject: Behaviour): Unit = ???

    override def disable(gameObject: Behaviour): Unit = ???

    override def create(gameObject: Behaviour): Unit =
      if gameObjects.exists(_ eq gameObject) then
        throw IllegalArgumentException("Cannot instantiate an object already instantiated")
      gameObjectsToAdd = gameObjectsToAdd :+ gameObject
      gameObject.onInit(this)
      if gameObject.enabled then gameObject.onEnabled(this)

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
      if !gameObjects.exists(_ eq gameObject) then
        throw IllegalArgumentException("Cannot destroy an object not instantiated")
      gameObjectsToRemove = gameObjectsToRemove :+ gameObject

    private def enabledObjects = gameObjects.filter(_.enabled)
      
    private def applyCreate(): Unit =
      gameObjects = gameObjects ++ gameObjectsToAdd
      gameObjectsToAdd.filter(_.enabled).foreach(_.onStart(this))
      gameObjectsToAdd = Seq()
      
    private def applyDestroy(): Unit =
      gameObjects = gameObjects.filterNot(gameObjectsToRemove.contains)
      gameObjectsToRemove.foreach(_.onDeinit(this))
      gameObjectsToRemove = Seq()

    private var shouldStop = false

    override def run(initialScene: Scene): Unit =
      shouldStop = false
      deltaTimeNanos = 0
      loadScene(initialScene)

      while !shouldStop do
        gameObjects = sceneToLoad.get()
        sceneToLoad = Option.empty

        gameObjects.foreach(_.onInit(this))

        enabledObjects.foreach(_.onEnabled(this))

        enabledObjects.foreach(_.onStart(this))

        while !shouldStop && sceneToLoad.isEmpty do
          val start = System.nanoTime()

          applyCreate()

          enabledObjects.foreach(_.onEarlyUpdate(this))

          enabledObjects.foreach(_.onUpdate(this))

          enabledObjects.foreach(_.onLateUpdate(this))

          applyDestroy()
          
          io.onFrameEnd(this)
          

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
  def apply(io: IO, storage: Storage): Engine =
    new EngineImpl(io = io, storage = storage)
