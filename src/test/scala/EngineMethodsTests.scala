import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*
import TestUtils.*
import TestUtils.Testers.*

class EngineMethodsTests extends AnyFlatSpec:
  val id0 = new Behaviour with Identifiable("0")
  val id1 = new Behaviour with Identifiable("1")
  val id2 = new Behaviour with Identifiable("2")
  val id3 = new Behaviour with Identifiable("3")

  val engine = Engine(new IO() {}, StorageMock())

  val scene1: Scene = () => Seq(id0, id1)
  val scene2: Scene = () => Seq(id2, id3)

  "loadScene" should "change the active scene" in:
    engine.testOnUpdate(scene1):
      engine.find[Identifiable]() should contain theSameElementsAs scene1()

      engine.testLoadSceneOnUpdate(scene2):
        engine.find[Identifiable]() should contain theSameElementsAs scene2()

  it should "change the scene after finishing the current frame" in:
    engine.testOnLifecycleEvent(scene1)(
      onEarlyUpdate =
        engine.testLoadSceneOnUpdate(scene2):
          engine.find[Identifiable]() should contain theSameElementsAs scene2(),

      onLateUpdate =
        engine.find[Identifiable]() should contain theSameElementsAs scene1()
    )
  