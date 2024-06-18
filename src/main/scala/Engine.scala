import scala.annotation.targetName
import Behaviours.*
import scala.reflect.TypeTest

trait Engine:
  val io: IO
  val storage: Storage
  def getCurrentNumSteps(): Int
  def loadScene(scene: Scene): Unit
  def enable(gameObject: Behaviour): Unit
  def disable(gameObject: Behaviour): Unit
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

object Engine:
  private class EngineImpl(
      override val io: IO,
      override val storage: Storage,
      private val gameObjects: Iterable[Behaviour],
      numSteps: Int,
      dtNanos: Long
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

    override def deltaTimeNanos: Long = ???

    override def destroy(gameObject: Behaviour): Unit = ???

    override def getCurrentNumSteps(): Int = currentStep

    private def enabledGameObjects =
      gameObjects.filter(gameObject => gameObject.enabled)

    private var currentStep = 0
    private var shouldStop = false

    override def run(): Unit =
      gameObjects.foreach(gameObject => gameObject.onInit(this))

      enabledGameObjects.foreach(gameObject => gameObject.onEnabled(this))

      enabledGameObjects.foreach(gameObject => gameObject.onStart(this))

      while !shouldStop && currentStep < numSteps do
        enabledGameObjects.foreach(gameObject => gameObject.onEarlyUpdate(this))

        enabledGameObjects.foreach(gameObject => gameObject.onUpdate(this))

        enabledGameObjects.foreach(gameObject => gameObject.onLateUpdate(this))
        currentStep = currentStep + 1

      gameObjects.foreach(gameObject => gameObject.onDeinit(this))

    override def stop(): Unit = shouldStop = true

  def apply(
      io: IO,
      storage: Storage,
      gameObjects: Iterable[Behaviour],
      numSteps: Int,
      deltaTimeNanos: Long
  ): Engine = new EngineImpl(
    io = io,
    storage = storage,
    gameObjects = gameObjects,
    numSteps = numSteps,
    dtNanos = deltaTimeNanos
  )
