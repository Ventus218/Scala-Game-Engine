package model.behaviours.enemies

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import model.logic.MovementStateImpl.*
import sge.testing.TestUtils.*
import model.behaviours.VisualRange
import model.logic.Direction
import Direction.*
import sge.core.*
import mocks.MockSwingIO
import config.Config.CHARACTERS_WIDTH
import config.Config.CHARACTERS_HEIGHT

class EnemiesVisualRangeTests extends AnyFlatSpec with BeforeAndAfterEach:
  val width: Double = CHARACTERS_WIDTH
  val height: Double = CHARACTERS_HEIGHT
  val visualRangeSize: Double = height * 2
  val enemy =
    new Enemy("patrol.png", initialDirection = TOP)(visualRangeSize =
      visualRangeSize
    )

  var engine = Engine(MockSwingIO(), Storage())
  val scene = () =>
    Seq(
      enemy
    )

  override protected def beforeEach(): Unit =
    engine = Engine(MockSwingIO(), Storage())

  "Enemies" should "have the right visual range dimensions at startup" in:
    test(engine) on scene soThat:
      _.onLateUpdate(
        engine.find[VisualRange]().head.shapeWidth shouldBe enemy.imageWidth
      )

  it should "have the right offset at startup" in:
    test(engine) on scene soThat:
      _.onLateUpdate(
        engine
          .find[VisualRange]()
          .head
          .positionOffset shouldBe (0, visualRangeSize / 2 + height / 2)
      )
