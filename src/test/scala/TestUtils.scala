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

  case class GameloopTestingBuilder(
      val init: () => TestingFunction = () => _ => (),
      val start: () => TestingFunction = () => _ => (),
      val earlyUpdate: () => TestingFunction = () => _ => (),
      val update: () => TestingFunction = () => _ => (),
      val lateUpdate: () => TestingFunction = () => _ => (),
      val deinit: () => TestingFunction = () => _ => (),
      val enabled: () => TestingFunction = () => _ => (),
      val disabled: () => TestingFunction = () => _ => (),
      val nFramesToRun: Int = 1
  ):
    def onInit(testingFunction: => TestingFunction): GameloopTestingBuilder =
      copy(init = () => testingFunction)

    def onStart(testingFunction: => TestingFunction): GameloopTestingBuilder =
      copy(start = () => testingFunction)

    def onEarlyUpdate(
        testingFunction: => TestingFunction
    ): GameloopTestingBuilder =
      copy(earlyUpdate = () => testingFunction)

    def onUpdate(testingFunction: => TestingFunction): GameloopTestingBuilder =
      copy(update = () => testingFunction)

    def onLateUpdate(
        testingFunction: => TestingFunction
    ): GameloopTestingBuilder =
      copy(lateUpdate = () => testingFunction)

    def onDeinit(testingFunction: => TestingFunction): GameloopTestingBuilder =
      copy(deinit = () => testingFunction)

    def onEnabled(testingFunction: => TestingFunction): GameloopTestingBuilder =
      copy(enabled = () => testingFunction)

    def onDisabled(
        testingFunction: => TestingFunction
    ): GameloopTestingBuilder =
      copy(disabled = () => testingFunction)

  import Testers.*
  import Behaviours.NFrameStopper

  private def gameloopTestingBuilderToTesterObject(
      testingBuilder: GameloopTestingBuilder
  ): Behaviour =
    new Behaviour
      with InitTester(testingBuilder.init()(_))
      with StartTester(testingBuilder.start()(_))
      with EarlyUpdateTester(testingBuilder.earlyUpdate()(_))
      with UpdateTester(testingBuilder.update()(_))
      with LateUpdateTester(testingBuilder.lateUpdate()(_))
      with DeinitTester(testingBuilder.deinit()(_))
      with EnabledTester(testingBuilder.enabled()(_))
      with DisabledTester(testingBuilder.disabled()(_))
      with NFrameStopper(testingBuilder.nFramesToRun)

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

    /** Runs the engine and calls the given test function on the corresponding
      * events by injecting a tester object into the scene.
      *
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param onXXX
      *   the function to execute at XXX event
      */
    def testOnGameloopEvents(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testingBuilder: GameloopTestingBuilder => GameloopTestingBuilder =
          (_) => GameloopTestingBuilder()
    ): Unit =
      engine.testWithTesterObject(scene):
        gameloopTestingBuilderToTesterObject(
          testingBuilder(GameloopTestingBuilder(nFramesToRun = nFramesToRun))
        )

    /** Loads a new scene on a running engine and calls the given test function
      * on the corresponding events by injecting a tester object into the scene.
      *
      * @param scene
      * @param nFramesToRun
      *   number of frames the engine will run, defaults to 1
      * @param onXXX
      *   the function to execute at XXX event
      */
    def loadSceneTestingOnGameloopEvents(
        scene: Scene = () => Seq.empty,
        nFramesToRun: Int = 1
    )(
        testingBuilder: GameloopTestingBuilder => GameloopTestingBuilder =
          (_) => GameloopTestingBuilder()
    ): Unit =
      engine.loadSceneWithTesterObject(scene):
        gameloopTestingBuilderToTesterObject(
          testingBuilder(GameloopTestingBuilder(nFramesToRun = nFramesToRun))
        )

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
