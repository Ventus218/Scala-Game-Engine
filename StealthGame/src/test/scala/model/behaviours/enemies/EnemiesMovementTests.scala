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

class EnemiesMovementTests extends AnyFlatSpec with BeforeAndAfterEach:
  val visualRangesize: Double = 10
  val width: Double = 5
  val height: Double = 3
  val enemy =
    new Enemy(width, height, (5, 5), "")(visualRangeSize = visualRangesize)

  var engine = Engine(MockSwingIO(), Storage())
  val scene = () =>
    Seq(
      enemy
    )

  override protected def beforeEach(): Unit =
    engine = Engine(MockSwingIO(), Storage())

  "Enemies" should "have the right visual range dimensions at startup" in:
    test(engine) on scene soThat:
      _.onEarlyUpdate(
        engine.find[VisualRange]().head.shapeWidth shouldBe (direction(
          initialMovement
        )._2 match
          case TOP    => enemy.imageWidth
          case BOTTOM => enemy.imageWidth
          case LEFT   => visualRangesize
          case RIGHT  => visualRangesize
        )
      )

  it should "have the right offset at startup" in:
    test(engine) on scene soThat:
      _.onEarlyUpdate(
        engine.find[VisualRange]().head.positionOffset shouldBe (direction(
          initialMovement
        )._2 match
          case TOP    => (0, visualRangesize / 2 + height / 2)
          case BOTTOM => (0, -visualRangesize / 2 - height / 2)
          case LEFT   => (-visualRangesize / 2 - width / 2, 0)
          case RIGHT  => (visualRangesize / 2 + width / 2, 0)
        )
      )
