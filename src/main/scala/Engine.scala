import scala.annotation.targetName
import Behaviours.*

trait Engine:
  val io: IO
  val storage: Storage
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
// TODO: add tests before decommenting
// extension (engine: Engine)
//   def deltaTimeSeconds: Double = engine.deltaTimeNanos / Math.pow(10, 9)
