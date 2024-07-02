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

  /** A function that accepts a TestingContext and performs some work */
  type TestingFunction = TestingContext => Unit

  // Below are defined some implicit convertions to allow a simpler test syntax
  // in this way the tester can use a simple block of code as well as a function
  // which has a TestingContext.
  // It may be an option to define just one implicit conversion from
  // Any to TestingFunciton, it's possible to do it in the future if needed.
  import org.scalatest.compatible.Assertion
  given Conversion[Assertion, TestingFunction] with
    def apply(x: Assertion): TestingFunction = _ => ()
  given Conversion[Unit, TestingFunction] with
    def apply(x: Unit): TestingFunction = _ => ()

  /** A builder for defining a testing function for each gameloop event and a
    * number of frames for the engine to run
    */
  opaque type TesterObjectBuilder = TesterObjectBuilderImpl
  case class TesterObjectBuilderImpl(
      val init: () => TestingFunction = () => (),
      val start: () => TestingFunction = () => (),
      val earlyUpdate: () => TestingFunction = () => (),
      val update: () => TestingFunction = () => (),
      val lateUpdate: () => TestingFunction = () => (),
      val deinit: () => TestingFunction = () => (),
      val enabled: () => TestingFunction = () => (),
      val disabled: () => TestingFunction = () => (),
      val nFramesToRun: Int = 1
  )

  import Testers.*
  import Behaviours.NFrameStopper
  extension (builder: TesterObjectBuilder)
    def onInit(testingFunction: => TestingFunction): TesterObjectBuilder =
      builder.copy(init = () => testingFunction)

    def onStart(testingFunction: => TestingFunction): TesterObjectBuilder =
      builder.copy(start = () => testingFunction)

    def onEarlyUpdate(
        testingFunction: => TestingFunction
    ): TesterObjectBuilder =
      builder.copy(earlyUpdate = () => testingFunction)

    def onUpdate(testingFunction: => TestingFunction): TesterObjectBuilder =
      builder.copy(update = () => testingFunction)

    def onLateUpdate(
        testingFunction: => TestingFunction
    ): TesterObjectBuilder =
      builder.copy(lateUpdate = () => testingFunction)

    def onDeinit(testingFunction: => TestingFunction): TesterObjectBuilder =
      builder.copy(deinit = () => testingFunction)

    def onEnabled(testingFunction: => TestingFunction): TesterObjectBuilder =
      builder.copy(enabled = () => testingFunction)

    def onDisabled(
        testingFunction: => TestingFunction
    ): TesterObjectBuilder =
      builder.copy(disabled = () => testingFunction)

    private def build: Behaviour =
      new Behaviour
        with InitTester(builder.init()(_))
        with StartTester(builder.start()(_))
        with EarlyUpdateTester(builder.earlyUpdate()(_))
        with UpdateTester(builder.update()(_))
        with LateUpdateTester(builder.lateUpdate()(_))
        with DeinitTester(builder.deinit()(_))
        with EnabledTester(builder.enabled()(_))
        with DisabledTester(builder.disabled()(_))
        with NFrameStopper(builder.nFramesToRun)

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

    /** Loads a scene on a running engine injecting the testerObject
      *
      * @param scene
      * @param testerObject
      */
    def loadSceneWithTesterObject(scene: Scene = () => Seq.empty)(
        testerObject: Behaviour
    ): Unit =
      engine.loadScene(scene.joined(() => Seq(testerObject)))

    /** Runs the engine and injects a tester object into the scene.
      *
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testerObjectBuilder
      *   a builder for defining the testing functions to execute
      */
    def testOnGameloopEvents(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testerObjectBuilder: TesterObjectBuilder => TesterObjectBuilder = (_) =>
          TesterObjectBuilderImpl(nFramesToRun = nFramesToRun)
    ): Unit =
      engine.testWithTesterObject(scene):
        testerObjectBuilder(
          TesterObjectBuilderImpl(nFramesToRun = nFramesToRun)
        ).build

    /** Loads a new scene on a running engine and injects a tester object into
      * the scene.
      *
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param testerObjectBuilder
      *   a builder for defining the testing functions to execute
      */
    def loadSceneTestingOnGameloopEvents(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testerObjectBuilder: TesterObjectBuilder => TesterObjectBuilder = (_) =>
          TesterObjectBuilderImpl(nFramesToRun = nFramesToRun)
    ): Unit =
      engine.loadSceneWithTesterObject(scene):
        testerObjectBuilder(
          TesterObjectBuilderImpl(nFramesToRun = nFramesToRun)
        ).build

  /** Provides multiple concrete behaviours for testing
    */
  object Testers:

    trait InitTester(testFunction: (TestingContext) => Unit) extends Behaviour:
      override def onInit: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onInit(engine)

    trait StartTester(testFunction: (TestingContext) => Unit) extends Behaviour:
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

    trait EnabledTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onEnabled: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onEnabled(engine)

    trait DisabledTester(testFunction: (TestingContext) => Unit)
        extends Behaviour:
      override def onDisabled: Engine => Unit = (engine) =>
        testFunction(TestingContext(engine, this))
        super.onDisabled(engine)
