import scala.annotation.targetName
import scala.reflect.TypeTest

trait Engine:
  val io: IO
  val storage: Storage
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

object Engine:
    private class EngineImpl(override val io: IO, override val storage: Storage, private val gameObjects: Iterable[GameObject[?]], numSteps: Int) extends Engine:
        override def enable(gameObject: GameObject[?]): Unit = ???

        override def find[B <: Behaviour](using tt: TypeTest[Behaviour, B])(): Iterable[GameObject[B]] = ???

        @targetName("find_object")
        override def find[G <: GameObject[?]](using tt: TypeTest[GameObject[?], G])(): Iterable[G] = ???

        override def destroy(gameObject: GameObject[?]): Unit = ???

        override def loadScene(scene: Scene): Unit = ???
        
        override def deltaTimeNanos: Long = ???
        
        override def findById[B <: Behaviour](using tt: TypeTest[Behaviour, B])(id: String): Option[GameObject[B]] = ???
        
        @targetName("find_object_by_id")
        override def findById[G <: GameObject[?]](using tt: TypeTest[GameObject[?], G])(id: String): Option[G] = ???
        
        override def create(gameObject: GameObject[?]): Unit = ???
        
        override def disable(gameObject: GameObject[?]): Unit = ???

        def enabledGameObjects = gameObjects.filter(gameObject => gameObject.enabled)

        override def run(): Unit = 
            gameObjects.foreach(
                _.behaviour.onInit(null)
            )

            enabledGameObjects.foreach(
                _.behaviour.onEnabled(null)
            )

            enabledGameObjects.foreach(
                _.behaviour.onStart(null)
            )

            for _ <- 0 until numSteps do
                enabledGameObjects.foreach(
                    _.behaviour.onEarlyUpdate(null)
                )

                enabledGameObjects.foreach(
                    _.behaviour.onUpdate(null)
                )

                enabledGameObjects.foreach(
                    _.behaviour.onLateUpdate(null)
                )

            gameObjects.foreach(
                _.behaviour.onDeinit(null)
            )

        override def stop(): Unit = ???

    def apply(io: IO, storage: Storage, gameObjects: Iterable[GameObject[?]], numSteps: Int): Engine = new EngineImpl(io = io, storage = storage, gameObjects = gameObjects, numSteps = numSteps)