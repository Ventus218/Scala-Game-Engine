package sge.core.mocks

import sge.core.IO

class IOMock extends IO:
    def isStopped = _isStopped
    private var _isStopped = false

    override def onEngineStop(): Unit = _isStopped = true