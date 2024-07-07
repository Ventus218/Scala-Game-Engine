package sge.core.metrics

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import Angle.*

class AngleTests extends AnyFlatSpec:

  "An Angle" should "be converted correctly" in:
    0.degrees shouldBe 0.radians
    180.degrees shouldBe Math.PI.radians
    -90.degrees shouldBe (-Math.PI / 2).radians
