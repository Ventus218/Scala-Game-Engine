import scala.annotation.targetName
import Behaviours.*

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
    private class EngineImpl(override val io: IO, override val storage: Storage, private val gameObjects: Iterable[GameObject[?]], numSteps: Int, dtNanos: Long) extends Engine:
        override def enable(gameObject: GameObject[?]): Unit = ???

        override def find[B <: Behaviour](using tt: TypeTest[Behaviour, B])(): Iterable[GameObject[B]] = ???

        @targetName("find_object")
        override def find[G <: GameObject[?]](using tt: TypeTest[GameObject[?], G])(): Iterable[G] = ???

        override def destroy(gameObject: GameObject[?]): Unit = ???

        override def loadScene(scene: Scene): Unit = ???
        
        override def deltaTimeNanos: Long = dtNanos
        
        override def findById[B <: Behaviour](using tt: TypeTest[Behaviour, B])(id: String): Option[GameObject[B]] = ???
        
        @targetName("find_object_by_id")
        override def findById[G <: GameObject[?]](using tt: TypeTest[GameObject[?], G])(id: String): Option[G] = ???
        
        override def create(gameObject: GameObject[?]): Unit = ???
        
        override def disable(gameObject: GameObject[?]): Unit = ???
        
        override def getCurrentNumSteps(): Int = currentStep
        
        private def enabledGameObjects = gameObjects.filter(gameObject => gameObject.enabled)
        
        private var currentStep = 0
        private var shouldStop = false

        override def run(): Unit =
            gameObjects.toContexts().foreach(context =>
                context.gameObject.behaviour.onInit(context)
            )

            enabledGameObjects.toContexts().foreach(context =>
                context.gameObject.behaviour.onEnabled(context)
            )

            enabledGameObjects.toContexts().foreach(context =>
                context.gameObject.behaviour.onStart(context)
            )

            while !shouldStop && currentStep < numSteps do
                enabledGameObjects.toContexts().foreach(context =>
                    context.gameObject.behaviour.onEarlyUpdate(context)
                )

                enabledGameObjects.toContexts().foreach(context =>
                    context.gameObject.behaviour.onUpdate(context)
                )

                enabledGameObjects.toContexts().foreach(context =>
                    context.gameObject.behaviour.onLateUpdate(context)
                )
                currentStep = currentStep + 1

            gameObjects.toContexts().foreach(context =>
                context.gameObject.behaviour.onDeinit(context)
            )

        override def stop(): Unit = shouldStop = true

        extension (gameObjects: Iterable[GameObject[?]])
            def toContexts(): Iterable[Context] = gameObjects.map(Context(this, _))

    def apply(io: IO, storage: Storage, gameObjects: Iterable[GameObject[?]], numSteps: Int, deltaTimeNanos: Long): Engine = new EngineImpl(io = io, storage = storage, gameObjects = gameObjects, numSteps = numSteps, dtNanos = deltaTimeNanos)
