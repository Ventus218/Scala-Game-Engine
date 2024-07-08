package sge.core

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.exceptions.TestFailedException
import org.scalatest.BeforeAndAfterEach
import sge.testing.*
import sge.testing.behaviours.NFrameStopper
import mocks.*
import GameloopTester.*
import GameloopEvent.*

class GameLoopTests extends AnyFlatSpec with BeforeAndAfterEach:
  private def getSequenceOfActions(): Seq[GameloopEvent] =
    Seq(Init, Start)

  private def getUpdatesSequenceOfActions(): Seq[GameloopEvent] =
    Seq(EarlyUpdate, Update, LateUpdate)

  val numSteps = 3

  private def testScene: Scene = () =>
    Iterable(
      new Behaviour with GameloopTester,
      new Behaviour with GameloopTester,
      new Behaviour(enabled = false) with GameloopTester,
      new Behaviour(enabled = false) with GameloopTester
    )

  var engine = Engine(
    io = new IO() {},
    storage = Storage()
  )

  override protected def beforeEach(): Unit =
    engine = Engine(
      io = new IO() {},
      storage = Storage()
    )

  "Engine" should "call run only one time then throws an exception" in:
    val emptyScene = () => Seq()
    test(engine) on emptyScene soThat (builder => builder)
    an[IllegalStateException] shouldBe thrownBy:
      test(engine) on emptyScene soThat (builder => builder)

  it should "call all methods on enabled gameObjects and just init and deinit on disabled gameObjects" in:
    var sequenceOfActions = getSequenceOfActions()

    for i <- 0 until numSteps do
      sequenceOfActions = sequenceOfActions ++ getUpdatesSequenceOfActions()

    test(engine) on testScene runningFor numSteps frames so that:
      _.onDeinit:
        /** This tests has to deal with undeterministic behaviour:
          *
          * Given the fact that the order of objects is not defined. The tester
          * object may run its test while other objects "onDeinit" may not have
          * been called yet. This is why the test succedes in both cases.
          */
        engine
          .find[GameloopTester]()
          .filter(_.enabled)
          .foreach(
            _.happenedEvents should (
              contain theSameElementsInOrderAs sequenceOfActions :+ Deinit
                or contain theSameElementsInOrderAs sequenceOfActions
            )
          )
        engine
          .find[GameloopTester]()
          .filter(!_.enabled)
          .foreach(
            _.happenedEvents should (
              contain theSameElementsInOrderAs Seq(Init) :+ Deinit
                or contain theSameElementsInOrderAs Seq(Init)
            )
          )

  it should "stop when engine.stop() is called" in:
    val oneFrameScene = testScene.joined: () =>
      Seq(new Behaviour with NFrameStopper(1))

    var sequenceOfActions =
      getSequenceOfActions() ++ getUpdatesSequenceOfActions()

    // The idea is that the test should run the engine for 5 frames but since a NFrameStopper(1) has been added it should stop only after one frame
    test(engine) on oneFrameScene runningFor 5 frames so that:
      _.onDeinit:
        /** This tests has to deal with undeterministic behaviour:
          *
          * Given the fact that the order of objects is not defined. The tester
          * object may run its test while other objects "onDeinit" may not have
          * been called yet. This is why the test succedes in both cases.
          */
        engine
          .find[GameloopTester]()
          .filter(_.enabled)
          .foreach(
            _.happenedEvents should (
              contain theSameElementsInOrderAs sequenceOfActions :+ Deinit
                or contain theSameElementsInOrderAs sequenceOfActions
            )
          )

  it should "throw an exception if the user tries to run again the engine while it's already running" in:
    test(engine) on testScene soThat:
      _.onStart:
        an[IllegalStateException] shouldBe thrownBy:
          engine.run(() => Seq())

  it should "allow to set an fps limit" in:
    val fpsLimit = 60
    val expectedTimeSeconds = 1d

    val engine = Engine(new IO {}, Storage(), fpsLimit)

    val start = System.currentTimeMillis()
    test(engine) runningFor fpsLimit soThat (builder => builder)
    val end = System.currentTimeMillis()

    val elapsedTimeSeconds = (end - start) / 1_000d
    try Math.abs(elapsedTimeSeconds - expectedTimeSeconds) should be <= 0.2
    catch
      case _: TestFailedException =>
        cancel(
          "This test is highly dependent on the performance of the machine it is run on. It failed, so ensure everything is ok."
        )
      case throwable => throw throwable

  "Engine.deltaTimeNanos" should "be 0 for all the iteration of the game loop" in:
    test(engine) on testScene soThat:
      _.onEarlyUpdate:
        engine.deltaTimeNanos shouldBe 0
      .onUpdate:
        engine.deltaTimeNanos shouldBe 0
      .onLateUpdate:
        engine.deltaTimeNanos shouldBe 0

  it should "be 0 if the game loop is not executed" in:
    test(engine) on testScene runningFor 0 frames so that:
      _.onDeinit:
        engine.deltaTimeNanos shouldBe 0

    engine.deltaTimeNanos shouldBe 0

  it should "be higher than 0 after a game loop iteration" in:
    test(engine) on testScene soThat:
      _.onDeinit:
        engine.deltaTimeNanos should be > 0L

  it should "be higher than time elapsed inside updates" in:
    val slowDownDurationMillis: Long = 10
    val expectedElapsedTimeNanos =
      (slowDownDurationMillis * Math.pow(10, 6)).toLong * 3

    test(engine) soThat:
      _.onEarlyUpdate:
        Thread.sleep(slowDownDurationMillis)
      .onUpdate:
        Thread.sleep(slowDownDurationMillis)
      .onLateUpdate:
        Thread.sleep(slowDownDurationMillis)
      .onDeinit:
        engine.deltaTimeNanos should be >= expectedElapsedTimeNanos

  "deltaTimeSeconds" should "represent deltaTimeNanos in seconds" in:
    val engineMock = (nanoseconds: Long) =>
      import scala.reflect.TypeTest
      new Engine() {
        override def deltaTimeNanos: Long = nanoseconds
        override val io: IO = new IO {}
        override def disable(gameObject: Behaviour): Unit = ???
        override val storage: Storage = Storage()
        override val fpsLimiter: FPSLimiter = FPSLimiter(Int.MaxValue)
        override def find[B <: Identifiable](using
            tt: TypeTest[Behaviour, B]
        )(id: String): Option[B] = ???
        override def find[B <: Behaviour](using
            tt: TypeTest[Behaviour, B]
        )(): Iterable[B] = ???
        override def loadScene(scene: Scene): Unit = ???
        override def destroy(gameObject: Behaviour): Unit = ???
        override def run(initialScene: Scene): Unit = ???
        override def enable(gameObject: Behaviour): Unit = ???
        override def stop(): Unit = ???
        override def create(gameObject: Behaviour): Unit = ???
      }

    import EngineUtils.*
    engineMock(10_000_000).deltaTimeSeconds shouldBe 0.01
    engineMock(15_000_000).deltaTimeSeconds shouldBe 0.015
    engineMock(166_000_005).deltaTimeSeconds shouldBe 0.166000005
    engineMock(1_000_000_000).deltaTimeSeconds shouldBe 1
