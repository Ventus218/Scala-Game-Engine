package sge.core.mock

import sge.core.*

object GameloopTester:
  enum GameloopEvent:
    case Init
    case Enable
    case Disable
    case Start
    case EarlyUpdate
    case Update
    case LateUpdate
    case Deinit

  trait GameloopTester extends Behaviour:
    import GameloopEvent.*

    private var _happenedEvents: Seq[GameloopEvent] = Seq()
    def happenedEvents: Seq[GameloopEvent] = _happenedEvents
    def happenedEvents_=(newValue: Seq[GameloopEvent]) =
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
