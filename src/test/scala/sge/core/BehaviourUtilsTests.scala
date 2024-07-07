package sge.core

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import mocks.*
import behaviours.dimension2d.*
import BehaviourUtils.*

class BehaviourUtilsTests extends AnyFlatSpec:
  val gom = GameObjectMock()
  val gomPositionable = new GameObjectMock with Positionable
  val id1 = new Behaviour with Identifiable("1")
  val id2 = new Behaviour with Identifiable("2")
  val positionable = new Behaviour with Positionable

  val iterable: Iterable[Behaviour] =
    Seq(gom, gomPositionable, id1, id2, positionable)

  "filter" should "filter by behaviours" in:
    iterable.filter[Positionable]() shouldBe Seq(gomPositionable, positionable)
    iterable.filter[Identifiable]() shouldBe Seq(id1, id2)
    iterable.filter[GameObjectMock]() shouldBe Seq(gom, gomPositionable)
    iterable.filter[Behaviour]() shouldBe iterable

  "find" should "retrieve the one behaviour with the given id" in:
    iterable.find[Identifiable](id1.id) shouldBe Option(id1)
    iterable.find[Identifiable]("3") shouldBe None
