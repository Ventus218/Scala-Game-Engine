import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class SceneTests extends AnyFlatSpec:
  val scene = Scene(() =>
    Set(
      GameObjectMock(id = Some("1")),
      GameObjectMock(id = Some("2")),
      GameObjectMock()
    )
  )

  "Scene" should "instantiate game objects" in:
    val instantiatedObjects = scene.gameObjects()
    instantiatedObjects.exists(_.id == Some("1")) shouldBe true
    instantiatedObjects.exists(_.id == Some("2")) shouldBe true
    instantiatedObjects.exists(_.id == None) shouldBe true
