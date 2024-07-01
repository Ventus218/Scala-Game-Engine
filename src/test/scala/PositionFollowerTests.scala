import org.scalatest.flatspec.AnyFlatSpec
import Dimensions2D.Positionable
import Dimensions2D.PositionFollower

class PositionFollowerTests extends AnyFlatSpec:
    "positionFollower" should "be created" in:
        val positionable = new Behaviour with Positionable
        new Behaviour with PositionFollower(position = positionable) with Positionable