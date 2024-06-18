import scala.annotation.targetName
import Behaviours.*
import scala.reflect.TypeTest

/** Takes care of starting and stopping the game loop, 
  * enable/disable Behaviours of the loaded Scene, create/destroy Behaviours of the loaded Scene,
  * find some Behaviours of the loaded Scene and gives the delta time in nanoseconds.
  */
trait Engine:
  /** The IO of the engine, used to render the view to the monitor and getting inputs by the user */
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

  def deltaTimeNanos: Long

  import scala.reflect.TypeTest

  def find[B <: Behaviour](using
      tt: TypeTest[Behaviour, B]
  )(): Iterable[B]

  def find[B <: Identifiable](using
      tt: TypeTest[Behaviour, B]
  )(id: String): Option[B]

object Engine:
  //gameObjects, numSteps and dtNanos are used just to test the Engine until other Interfaces are implemented
  private class EngineImpl(
      override val io: IO,
      override val storage: Storage,
      gameObjects: Iterable[Behaviour],
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
      currentStep = 0

    override def stop(): Unit = shouldStop = true

  var engine: Engine = null

  /** Instantiate the engine. 
    * Throws Illegal State Excpetion if it is called more than one time.
    *
    * @param io The IO of the engine, used to render the view to the monitor and getting inputs by the user
    * @param storage The Storage that the engine will use to store values between scenes
    */
  def intantiate(
      io: IO,
      storage: Storage,
      gameObjects: Iterable[Behaviour],
      numSteps: Int,
      deltaTimeNanos: Long
  ): Unit =
    if engine == null then
      engine = new EngineImpl(
        io = io,
        storage = storage,
        gameObjects = gameObjects,
        numSteps = numSteps,
        dtNanos = deltaTimeNanos
      )
    else
      throw new IllegalStateException("Engine alrady instantiated")

  /** Returns always the same instance of the engine if instantiated, otherwise
    * throws an IllegalStateException
    * 
    * @return the instance of the engine
    */
  def apply(): Engine = if engine != null then engine else throw new IllegalStateException("Engine not yet instantiated")