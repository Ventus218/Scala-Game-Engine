import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.{*, given}
import LifecycleTester.*
import LifecycleEvent.*
import TestUtils.Testers.*

class EngineObjectCreationTests extends AnyFlatSpec:
  val obj1 = new GameObjectMock() with Identifiable("1")
  val obj2 = new GameObjectMock() with Identifiable("2")
  val obj3 = new GameObjectMock() with Identifiable("3")
  val engine = Engine(new IO() {}, Storage())

  val scene: Scene = () => Seq(obj1, obj2, obj3)

  "create" should "instantiate a game object in the scene" in:
    engine.testOnLifecycleEvent()(
      onStart = engine.create(obj1),
      onDeInit = engine.find[GameObjectMock]() should contain only (obj1)
    )

  it should "not instantiate a game object between EarlyUpdate, Update and LateUpdate, but after" in:
    var frame = 0
    engine.testOnLifecycleEvent(nFramesToRun = 2)(
      onEarlyUpdate =
        frame += 1
        if frame > 1 then
          engine.find[GameObjectMock]() should contain only (obj1, obj2, obj3)
        else
          engine.find[GameObjectMock]() shouldBe empty
          engine.create(obj1)
      ,
      onUpdate = if frame <= 1 then
        engine.find[GameObjectMock]() shouldBe empty
        engine.create(obj2),
      onLateUpdate = if frame <= 1 then
        engine.find[GameObjectMock]() shouldBe empty
        engine.create(obj3),
    )

  it should "not instantiate a game object already present in the scene" in:
    engine.testOnLifecycleEvent(scene)(
      onUpdate =
        engine.find[GameObjectMock]() should contain(obj1)
        an[IllegalArgumentException] shouldBe thrownBy {
          engine.create(obj1)
        }
    )

  it should "throw if trying to create an object multiple times in the same frame" in:
    engine.testOnLifecycleEvent(scene)(
      onUpdate =
        val obj = new Behaviour {}
        engine.create(obj)
        an[IllegalArgumentException] shouldBe thrownBy:
          engine.create(obj)
    )

  it should "invoke the method onInit on the game object" in:
    val obj = new Behaviour with LifecycleTester

    engine.testOnLifecycleEvent(scene)(
      onStart = engine.create(obj),
      onDeInit = obj.happenedEvents should contain oneElementOf Seq(Init)
    )

  it should "invoke the event methods in the correct order" in:
    val objTester: LifecycleTester =
      new Behaviour with LifecycleTester
    engine.testOnLifecycleEvent(scene)(
      onStart = engine.create(objTester)
    )

    objTester.happenedEvents should contain theSameElementsInOrderAs
      Seq(Init, Start, EarlyUpdate, Update, LateUpdate, Deinit)

  "destroy" should "remove a game object from the scene" in:
    engine.testOnLifecycleEvent(scene)(
      onStart = engine.destroy(obj1),
      onDeInit = engine.find[GameObjectMock]() should contain only (obj2, obj3)
    )

  it should "not remove a game object between EarlyUpdate, Update and LateUpdate, but after" in:
    var frame = 0
    engine.testOnLifecycleEvent(scene, nFramesToRun = 2)(
      onEarlyUpdate =
        frame += 1
        if frame > 1 then engine.find[GameObjectMock]() shouldBe empty
        else
          engine.find[GameObjectMock]() should contain theSameElementsAs scene()
          engine.destroy(obj1)
      ,
      onUpdate = if frame <= 1 then
        engine.find[GameObjectMock]() should contain theSameElementsAs scene()
        engine.destroy(obj2),
      onLateUpdate = if frame <= 1 then
        engine.find[GameObjectMock]() should contain theSameElementsAs scene()
        engine.destroy(obj3),
    )

  it should "remove an object only if already instantiated in the scene" in:
    engine.testOnLifecycleEvent(scene)(
      onStart =
        an[IllegalArgumentException] shouldBe thrownBy {
          engine.destroy(new GameObjectMock())
        }
        an[IllegalArgumentException] shouldBe thrownBy {
          engine.destroy(new GameObjectMock() with Identifiable("1"))
        }
    )

  it should "throw if trying to destroy an object multiple times in the same frame" in:
    engine.testOnLifecycleEvent(scene)(
      onUpdate =
        engine.destroy(obj1)
        an[IllegalArgumentException] shouldBe thrownBy:
          engine.destroy(obj1)
    )

  it should "invoke the method onDeinit on the game object at the end of the frame" in:
    val obj = new Behaviour with LifecycleTester()

    engine.testOnLifecycleEvent(() => Seq(obj), nFramesToRun = 3)(
      onStart = engine.destroy(obj),
      onDeInit = obj.happenedEvents should contain theSameElementsInOrderAs Seq(
        Init,
        Start,
        EarlyUpdate,
        Update,
        LateUpdate,
        Deinit
      )
    )
