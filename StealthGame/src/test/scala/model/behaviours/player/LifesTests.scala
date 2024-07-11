package model.behaviours.player

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import mocks.MockSwingIO
import sge.core.*
import sge.testing.TestUtils.test

class LifesTests extends AnyFlatSpec with BeforeAndAfterEach:
  val emptyScene: Scene = () => Seq()
  var player = Player(
    currentScene = emptyScene,
    nextScene = emptyScene,
    initialPosition = (0, 0)
  )()

  override protected def beforeEach(): Unit =
    player = Player(
      currentScene = emptyScene,
      nextScene = emptyScene,
      initialPosition = (0, 0)
    )()

  "Player lifes" should "be initialized as Lifes on storage" in:
    val engine: Engine = Engine(MockSwingIO(), Storage())
    val myScene: Scene = () =>
      Seq(
        player
      )

    val lifes = 5

    engine.storage.set("Lifes", lifes)

    test(engine) on myScene soThat:
      _.onStart(
        player.lifes shouldBe lifes
      )

  it should "be able to change" in:
    player.lifes shouldBe 0

    player.lifes = 2
    player.lifes shouldBe 2

    player.lifes = 5
    player.lifes shouldBe 5

  it should "not change to negative values" in:
    an[IllegalArgumentException] shouldBe thrownBy:
      player.lifes = -5
