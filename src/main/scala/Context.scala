trait Context:
  val engine: Engine
  val gameObject: GameObject[?]

extension (context: Context)
  def io: IO = context.engine.io
  def storage: Storage = context.engine.storage
  def deltaTimeNanos: Long = context.engine.deltaTimeNanos
  def deltaTimeSeconds: Double = context.deltaTimeNanos / (Math.pow(10, 9))

// Examples

object Context:
  private case class ContextImpl(engine: Engine, gameObject: GameObject[?])
      extends Context

  def apply(engine: Engine, gameObject: GameObject[?]): Context =
    ContextImpl(engine, gameObject)
