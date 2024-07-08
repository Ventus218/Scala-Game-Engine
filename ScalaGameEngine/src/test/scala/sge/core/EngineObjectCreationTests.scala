package sge.core

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import sge.testing.*
import mocks.*
import GameloopTester.*
import GameloopEvent.*

class EngineObjectCreationTests extends AnyFlatSpec with BeforeAndAfterEach:
  val obj1 = new GameObjectMock() with Identifiable("1")
  val obj2 = new GameObjectMock() with Identifiable("2")
  val obj3 = new GameObjectMock() with Identifiable("3")
  var engine = Engine(
    io = new IO() {},
    storage = Storage()
  )

  override protected def beforeEach(): Unit = 
    engine = Engine(
      io = new IO() {},
      storage = Storage()
    )

  val scene: Scene = () => Seq(obj1, obj2, obj3)

  "create" should "instantiate a game object in the scene" in:
    test(engine) soThat:
      _.onStart:
        engine.create(obj1)
      .onDeinit:
        engine.find[GameObjectMock]() should contain only (obj1)

  it should "not instantiate a game object between EarlyUpdate, Update and LateUpdate, but after" in:
    var frame = 0
    test(engine) runningFor 2 frames so that:
      _.onEarlyUpdate:
        frame += 1
        if frame > 1 then
          engine.find[GameObjectMock]() should contain only (obj1, obj2, obj3)
        else
          engine.find[GameObjectMock]() shouldBe empty
          engine.create(obj1)
      .onUpdate:
        if frame <= 1 then
          engine.find[GameObjectMock]() shouldBe empty
          engine.create(obj2)
      .onLateUpdate:
        if frame <= 1 then
          engine.find[GameObjectMock]() shouldBe empty
          engine.create(obj3)

  it should "not instantiate a game object already present in the scene" in:
    test(engine) on scene soThat:
      _.onUpdate:
        engine.find[GameObjectMock]() should contain(obj1)
        an[IllegalArgumentException] shouldBe thrownBy {
          engine.create(obj1)
        }

  it should "throw if trying to create an object multiple times in the same frame" in:
    test(engine) on scene soThat:
      _.onUpdate:
        val obj = new Behaviour {}
        engine.create(obj)
        an[IllegalArgumentException] shouldBe thrownBy:
          engine.create(obj)

  it should "invoke the method onInit on the game object" in:
    val obj = new Behaviour with GameloopTester

    test(engine) on scene soThat:
      _.onStart:
        engine.create(obj)
      .onDeinit:
        obj.happenedEvents should contain oneElementOf Seq(Init)

  it should "invoke the event methods in the correct order" in:
    val objTester: GameloopTester =
      new Behaviour with GameloopTester
    test(engine) on scene soThat:
      _.onStart:
        engine.create(objTester)

    objTester.happenedEvents should contain theSameElementsInOrderAs
      Seq(Init, Start, EarlyUpdate, Update, LateUpdate, Deinit)

  "destroy" should "remove a game object from the scene" in:
    test(engine) on scene soThat:
      _.onStart:
        engine.destroy(obj1)
      .onDeinit:
        engine.find[GameObjectMock]() should contain only (obj2, obj3)

  it should "not remove a game object between EarlyUpdate, Update and LateUpdate, but after" in:
    var frame = 0
    test(engine) on scene runningFor 2 frames so that:
      _.onEarlyUpdate:
        frame += 1
        if frame > 1 then engine.find[GameObjectMock]() shouldBe empty
        else
          engine.find[GameObjectMock]() should contain theSameElementsAs scene()
          engine.destroy(obj1)
      .onUpdate:
        if frame <= 1 then
          engine.find[GameObjectMock]() should contain theSameElementsAs scene()
          engine.destroy(obj2)
      .onLateUpdate:
        if frame <= 1 then
          engine.find[GameObjectMock]() should contain theSameElementsAs scene()
          engine.destroy(obj3)

  it should "remove an object only if already instantiated in the scene" in:
    test(engine) on scene soThat:
      _.onStart:
        an[IllegalArgumentException] shouldBe thrownBy {
          engine.destroy(new GameObjectMock())
        }
        an[IllegalArgumentException] shouldBe thrownBy {
          engine.destroy(new GameObjectMock() with Identifiable("1"))
        }

  it should "throw if trying to destroy an object multiple times in the same frame" in:
    test(engine) on scene soThat:
      _.onUpdate:
        engine.destroy(obj1)
        an[IllegalArgumentException] shouldBe thrownBy:
          engine.destroy(obj1)

  it should "invoke the method onDeinit on the game object at the end of the frame" in:
    val obj = new Behaviour with GameloopTester()
    val scene = () => Seq(obj)

    test(engine) on scene runningFor 3 frames so that:
      _.onStart:
        engine.destroy(obj)
      .onDeinit:
        obj.happenedEvents should contain theSameElementsInOrderAs Seq(
          Init,
          Start,
          EarlyUpdate,
          Update,
          LateUpdate,
          Deinit
        )
