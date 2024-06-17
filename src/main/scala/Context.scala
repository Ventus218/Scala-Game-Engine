trait Context:
  val engine: Engine
  val gameObject: GameObject[?]

extension (c: Context)
  def io: IO = c.engine.io
  def storage: Storage = c.engine.storage
  def deltaTimeNanos: Long = c.engine.deltaTimeNanos
  def deltaTimeSeconds: Double = c.engine.deltaTimeNanos / Math.pow(10, 9)

object Context:
  private case class ContextImpl(engine: Engine, gameObject: GameObject[?])
      extends Context

  def apply(engine: Engine, gameObject: GameObject[?]): Context =
    ContextImpl(engine, gameObject)
