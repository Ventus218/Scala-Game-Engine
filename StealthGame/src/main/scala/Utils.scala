import sge.core.Behaviour
import sge.core.Engine
import sge.core.behaviours.Identifiable
import scala.reflect.TypeTest

extension (engine: Engine)
  def setEnableAll[B <: Behaviour](enable: Boolean)(using
      tt: TypeTest[Behaviour, B]
  ): Unit =
    engine
      .find[B]()
      .foreach(behaviour =>
        if enable then engine.enable(behaviour) else engine.disable(behaviour)
      )

  def setEnable[B <: Identifiable](id: String, enable: Boolean)(using
      tt: TypeTest[Behaviour, B]
  ): Unit =
    val behaviour = engine.find[B](id).get
    if enable then engine.enable(behaviour) else engine.disable(behaviour)
