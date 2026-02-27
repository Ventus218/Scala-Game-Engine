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
import sge.swing.output.Animations.AnimationController
import sge.swing.output.Animations.Animation

class EnemiesVisualRangeTests extends AnyFlatSpec with BeforeAndAfterEach:
  val width: Double = CHARACTERS_WIDTH
  val height: Double = CHARACTERS_HEIGHT
  val visualRangeSize: Double = height * 2
  val enemy =
    new Enemy(AnimationController.builder().addAnimation("patrol", Animation.uniform(Seq("patrol.png"), 0.1, false)).withDefault("patrol"), initialDirection = TOP)(visualRangeSize =
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
        engine.find[VisualRange]().head.shapeWidth shouldBe enemy.animationWidth
      )

  it should "have the right offset at startup" in:
    test(engine) on scene soThat:
      _.onLateUpdate(
        engine
          .find[VisualRange]()
          .head
          .positionOffset shouldBe (0, visualRangeSize / 2 + height / 2)
      )
