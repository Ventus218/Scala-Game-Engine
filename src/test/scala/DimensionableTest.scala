import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Behaviours.*

class DimensionableTest extends AnyFlatSpec:
    "dimensionable" should "be a mixin of Behaviour" in:
        new Behaviour with Dimensionable()