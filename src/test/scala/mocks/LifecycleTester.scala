object LifecycleTester:
  enum LifecycleEvent:
    case Init
    case Enable
    case Disable
    case Start
    case EarlyUpdate
    case Update
    case LateUpdate
    case Deinit

  trait LifecycleTester extends Behaviour:
    import LifecycleEvent.*

    private var _happenedEvents: Seq[LifecycleEvent] = Seq()
    def happenedEvents: Seq[LifecycleEvent] = _happenedEvents
    def happenedEvents_=(newValue: Seq[LifecycleEvent]) =
      _happenedEvents = newValue

    override def onInit: Engine => Unit =
      _ => happenedEvents = Seq() :+ Init

    override def onEnabled: Engine => Unit =
      _ => happenedEvents = happenedEvents :+ Enable

    override def onDisabled: Engine => Unit =
      _ => happenedEvents = happenedEvents :+ Disable

    override def onStart: Engine => Unit =
      _ => happenedEvents = happenedEvents :+ Start

    override def onEarlyUpdate: Engine => Unit =
      _ => happenedEvents = happenedEvents :+ EarlyUpdate

    override def onUpdate: Engine => Unit =
      _ => happenedEvents = happenedEvents :+ Update

    override def onLateUpdate: Engine => Unit =
      _ => happenedEvents = happenedEvents :+ LateUpdate

    override def onDeinit: Engine => Unit =
      _ => happenedEvents = happenedEvents :+ Deinit
