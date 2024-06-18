import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class SceneTests extends AnyFlatSpec:
  private def makeMockWithId(id: String) =
    new GameObjectMock with Identifiable(id)

  val scene = Scene(() =>
    Set(
      makeMockWithId("1"),
      makeMockWithId("2"),
      makeMockWithId("3")
    )
  )

  "Scene" should "instantiate game objects" in:
    val instantiatedObjects = scene.gameObjects()
    instantiatedObjects.exists(_ == makeMockWithId("1")) shouldBe true
    instantiatedObjects.exists(_ == makeMockWithId("2")) shouldBe true
    instantiatedObjects.exists(_ == makeMockWithId("3")) shouldBe true
