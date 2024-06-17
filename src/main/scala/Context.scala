trait Context:
  val engine: Engine
  val gameObject: GameObject[?]

object Context:
  private case class ContextImpl(engine: Engine, gameObject: GameObject[?])
      extends Context

  def apply(engine: Engine, gameObject: GameObject[?]): Context =
    ContextImpl(engine, gameObject)
