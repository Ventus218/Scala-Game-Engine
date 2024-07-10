package sge.core

import scala.annotation.targetName
import behaviours.Identifiable
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

  /** An object which allows to limit the engine fps */
  val fpsLimiter: FPSLimiter

  def loadScene(scene: Scene): Unit

  /** Enables the given object (only if not enabled). A call to the object
    * onEnabled will be done at the beginning of the next frame
    */
  def enable(gameObject: Behaviour): Unit

  /** Disables the given object (only if enabled). The call to the object
    * onDisabled will be done at the end of the frame and the object will be
    * disabled only in the next frame
    */
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
  private class EngineImpl(
      override val io: IO,
      override val storage: Storage,
      fpsLimit: Int
  ) extends Engine:

    private var gameObjects: Iterable[Behaviour] = Seq()
    private var sceneToLoad: Option[Scene] = Option.empty

    private var gameObjectsToAdd: Set[Behaviour] = Set()
    private var gameObjectsToRemove: Set[Behaviour] = Set()

    private var gameObjectsToEnable: Set[Behaviour] = Set()
    private var gameObjectsToDisable: Set[Behaviour] = Set()

    override def loadScene(scene: Scene): Unit =
      sceneToLoad = Option(scene)

    override val fpsLimiter: FPSLimiter = FPSLimiter(fpsLimit)

    override def enable(gameObject: Behaviour): Unit =
      if !gameObject.enabled then
        gameObjectsToEnable = gameObjectsToEnable + gameObject

    private def enableObjectsToBeEnabled(): Unit =
      gameObjectsToEnable.foreach: o =>
        o.enabled = true
        o.onEnabled(this)
      gameObjectsToEnable.foreach: o =>
        o.onStart(this)
      gameObjectsToEnable = Set.empty

    override def disable(gameObject: Behaviour): Unit =
      if gameObject.enabled then
        gameObjectsToDisable = gameObjectsToDisable + gameObject

    private def disableObjectsToBeDisabled(): Unit =
      gameObjectsToDisable.foreach: o =>
        o.enabled = false
        o.onDisabled(this)
      gameObjectsToDisable = Set.empty

    override def create(gameObject: Behaviour): Unit =
      if gameObjects.exists(_ eq gameObject) || gameObjectsToAdd(gameObject)
      then
        throw IllegalArgumentException(
          "Cannot instantiate an object already instantiated"
        )
      gameObjectsToAdd = gameObjectsToAdd + gameObject

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
      if !gameObjects.exists(_ eq gameObject) || gameObjectsToRemove(gameObject)
      then
        throw IllegalArgumentException(
          "Cannot destroy an object not instantiated"
        )
      gameObjectsToRemove = gameObjectsToRemove + gameObject

    private def enabledObjects = gameObjects.filter(_.enabled)

    private def applyCreate(): Unit =
      gameObjects = gameObjects ++ gameObjectsToAdd
      val created = Set.from(gameObjectsToAdd)
      gameObjectsToAdd = Set.empty
      created.foreach(_.onInit(this))
      created.filter(_.enabled).foreach(_.onStart(this))

    private def applyDestroy(): Unit =
      gameObjects = gameObjects.filterNot(gameObjectsToRemove.contains)
      val destroyed = Set.from(gameObjectsToRemove)
      gameObjectsToRemove = Set.empty
      destroyed.foreach(_.onDeinit(this))

    private var shouldStop = false
    private var alreadyStarted = false

    override def run(initialScene: Scene): Unit =
      if alreadyStarted then
        throw IllegalStateException("Engine has already been started")
      alreadyStarted = true

      deltaTimeNanos = 0
      loadScene(initialScene)

      while !shouldStop do
        gameObjects = sceneToLoad.get()
        sceneToLoad = Option.empty

        gameObjects.foreach(_.onInit(this))

        enabledObjects.foreach(_.onStart(this))

        while !shouldStop && sceneToLoad.isEmpty do
          val start = System.nanoTime()

          applyCreate()

          enableObjectsToBeEnabled()

          enabledObjects.foreach(_.onEarlyUpdate(this))

          enabledObjects.foreach(_.onUpdate(this))

          enabledObjects.foreach(_.onLateUpdate(this))

          disableObjectsToBeDisabled()

          applyDestroy()

          io.onFrameEnd(this)

          fpsLimiter.sleepToRespectFPSLimit(start)
          fpsLimiter.onFrameEnd()

          deltaTimeNanos = System.nanoTime() - start

        gameObjects.foreach(_.onDeinit(this))

      io.onEngineStop()

    override def stop(): Unit = shouldStop = true

  /** Creates the engine.
    *
    * @param io
    *   The IO of the engine, used to render the view to the monitor and getting
    *   inputs by the user
    * @param storage
    *   The Storage that the engine will use to store values between scenes
    * @param fpsLimit
    *   The maximum number of frames per second that the engine will compute
    */

  def apply(io: IO, storage: Storage, fpsLimit: Int = 60): Engine =
    new EngineImpl(io = io, storage = storage, fpsLimit = fpsLimit)
