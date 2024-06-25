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

    // Add new test utilities here if needed.
    
    /** Loads a new scene and calls `testFunction` on every Init
      *
      * @param newScene
      * @param nFramesToRun
      * number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testLoadSceneOnInit(newScene: Scene, nFramesToRun: Int = 1)(
      testFunction: => Unit
    ): Unit =
      val testerObject = new Behaviour
        with InitTester((_) => testFunction)
        with NFrameStopper(nFramesToRun)
      engine.loadScene(newScene.joined(() => Seq(testerObject)))

    /** Loads a new scene and calls `testFunction` on every Enabled
      *
      * @param newScene
      * @param nFramesToRun
      * number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testLoadSceneOnEnabled(newScene: Scene, nFramesToRun: Int = 1)(
      testFunction: => Unit
    ): Unit =
      val testerObject = new Behaviour
        with EnabledTester((_) => testFunction)
        with NFrameStopper(nFramesToRun)
      engine.loadScene(newScene.joined(() => Seq(testerObject)))
        
    /** Loads a new scene and calls `testFunction` on every Start
      *
      * @param newScene
      * @param nFramesToRun
      * number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testLoadSceneOnStart(newScene: Scene, nFramesToRun: Int = 1)(
      testFunction: => Unit
    ): Unit =
      val testerObject = new Behaviour
        with StartTester((_) => testFunction)
        with NFrameStopper(nFramesToRun)
      engine.loadScene(newScene.joined(() => Seq(testerObject)))

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
      val testerObject = new Behaviour
        with EarlyUpdateTester(testFunction)
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
      val testerObject = new Behaviour
        with UpdateTester(testFunction)
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
        
    /** Loads a new scene and calls `testFunction` on every Update
      *
      * @param newScene
      * @param nFramesToRun
      * number of frames the engine will run, defaults to 1
      * @param testFunction
      */
    def testLoadSceneOnUpdate(newScene: Scene, nFramesToRun: Int = 1)(
        testFunction: => Unit
    ): Unit =
      val testerObject = new Behaviour
        with UpdateTester((_) => testFunction)
        with NFrameStopper(nFramesToRun)
      engine.loadScene(newScene.joined(() => Seq(testerObject)))
      
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
      val testerObject = new Behaviour
        with LateUpdateTester(testFunction)
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
      val testerObject = new Behaviour
        with DeinitTester(testFunction)
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

    /** Runs the engine and calls the given test function on the corresponding events
      *
      * @param scene
      * @param nFramesToRun
      * number of frames the engine will run, defaults to 1
      * @param onEvent
      */
    def testOnLifecycleEvent(scene: Scene = () => Seq.empty, nFramesToRun: Int = 1)(
        onInit: => Unit = (),
        onEnabled: => Unit = (),
        onStart: => Unit = (),
        onEarlyUpdate: => Unit = (),
        onUpdate: => Unit = (),
        onLateUpdate: => Unit = (),
        onDeInit: => Unit = ()
    ): Unit =
      engine.testWithTesterObject(scene)(
        new Behaviour
          with InitTester((_) => onInit)
          with EnabledTester((_) => onInit)
          with StartTester((_) => onStart)
          with EarlyUpdateTester((_) => onEarlyUpdate)
          with UpdateTester((_) => onUpdate)
          with LateUpdateTester((_) => onLateUpdate)
          with DeinitTester((_) => onDeInit)
          with NFrameStopper(nFramesToRun)
      )

  /** Provides multiple concrete behaviours for testing
    */
  object Testers:

    // Add new testers here if needed.

    trait InitTester(testFunction: (TestingContext) => Unit)
      extends Behaviour:
      override def onInit: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onInit(engine)

    trait EnabledTester(testFunction: (TestingContext) => Unit)
      extends Behaviour:
      override def onEnabled: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onEnabled(engine)

    trait StartTester(testFunction: (TestingContext) => Unit)
      extends Behaviour:
      override def onStart: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onStart(engine)
        
    trait EarlyUpdateTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onEarlyUpdate: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onEarlyUpdate(engine)

    trait UpdateTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onUpdate: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onUpdate(engine)

    trait LateUpdateTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onLateUpdate: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onLateUpdate(engine)

    trait DeinitTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onDeinit: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onDeinit(engine)
