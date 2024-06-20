/** Utils to test Engine while it's running. These utils allow to inject a
  * testerObject into the engine to that it can run tests in the various hooks
  * of the engine game loop
  */
object TestUtils:
  /** Testing context contains information about the test that is being run
    *
    * @param engine
    * @param testerObject
    *   the object which was injected. It may be useful to take it into account
    *   in some tests.
    */
  case class TestingContext(
      val engine: Engine,
      val testerObject: Behaviour
  )

  import Testers.*
  import Behaviours.NFrameStopper

  extension (engine: Engine)
    /** Runs the engine with a given scene injecting the testerObject
      *
      * @param scene
      * @param testerObject
      */
    def testWithTesterObject(scene: Scene = () => Seq.empty)(
        testerObject: Behaviour
    ): Unit =
      engine.run(scene.joined(() => Seq(testerObject)))

    /** Runs the engine and calls `testFunction` on every EarlyUpdate
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      *   provides the current `TestingContext`
      */
    def testOnEarlyUpdateWithContext(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testFunction: (TestingContext) => Unit
    ): Unit =
      val testerObject = new EarlyUpdateTester(testFunction)
        with NFrameStopper(nFramesToRun)
      engine.testWithTesterObject(scene)(testerObject)

    /** Runs the engine and calls `testFunction` on every EarlyUpdate
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testOnEarlyUpdate(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testFunction: => Unit
    ): Unit =
      engine.testOnEarlyUpdateWithContext(scene, nFramesToRun): (_) =>
        testFunction

    /** Runs the engine and calls `testFunction` on every Update
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      *   provides the current `TestingContext`
      */
    def testOnUpdateWithContext(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testFunction: (TestingContext) => Unit
    ): Unit =
      val testerObject = new UpdateTester(testFunction)
        with NFrameStopper(nFramesToRun)
      engine.testWithTesterObject(scene)(testerObject)

    /** Runs the engine and calls `testFunction` on every Update
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testOnUpdate(scene: Scene = () => Seq.empty, nFramesToRun: Int = 1)(
        testFunction: => Unit
    ): Unit =
      engine.testOnUpdateWithContext(scene, nFramesToRun): (_) =>
        testFunction

    /** Runs the engine and calls `testFunction` on every LateUpdate
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      *   provides the current `TestingContext`
      */
    def testOnLateUpdateWithContext(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testFunction: (TestingContext) => Unit
    ): Unit =
      val testerObject = new LateUpdateTester(testFunction)
        with NFrameStopper(nFramesToRun)
      engine.testWithTesterObject(scene)(testerObject)

    /** Runs the engine and calls `testFunction` on every LateUpdate
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testOnLateUpdate(scene: Scene = () => Seq.empty, nFramesToRun: Int = 1)(
        testFunction: => Unit
    ): Unit =
      engine.testOnLateUpdateWithContext(scene, nFramesToRun): (_) =>
        testFunction

    /** Runs the engine and calls `testFunction` on Deinit
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      *   provides the current `TestingContext`
      */
    def testOnDeinitWithContext(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testFunction: (TestingContext) => Unit
    ): Unit =
      val testerObject = new DeinitTester(testFunction)
        with NFrameStopper(nFramesToRun)
      engine.testWithTesterObject(scene)(testerObject)

    /** Runs the engine and calls `testFunction` on Deinit
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testOnDeinit(scene: Scene = () => Seq.empty, nFramesToRun: Int = 1)(
        testFunction: => Unit
    ): Unit =
      engine.testOnDeinitWithContext(scene, nFramesToRun): (_) =>
        testFunction

  /** Provides multiple concrete behaviours for testing
    */
  object Testers:

    class EarlyUpdateTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onEarlyUpdate: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onEarlyUpdate(engine)

    class UpdateTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onUpdate: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onUpdate(engine)

    class LateUpdateTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onLateUpdate: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onLateUpdate(engine)

    class DeinitTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onDeinit: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onDeinit(engine)
