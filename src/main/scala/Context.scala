import scala.annotation.targetName

trait Context:
  val engine: Engine
  val gameObject: GameObject[?]

  def io: IO = engine.io

// Examples

object Context:
  private case class ContextImpl(engine: Engine, gameObject: GameObject[?])
      extends Context

  def apply(engine: Engine, gameObject: GameObject[?]): Context =
    ContextImpl(engine, gameObject)
