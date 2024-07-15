package entities

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class HealthTests extends AnyFlatSpec:
  def health(h: Int, _onHit: => Unit = (), _onDeath: => Unit = ()): Health = new Health(h):
    override protected def onHit(): Unit = _onHit
    override protected def onDeath(): Unit = _onDeath

  "Health" should "be initialized correctly" in :
    val h = health(10)
    h.health shouldBe 10

  it should "never have negative health" in :
    an [IllegalArgumentException] shouldBe thrownBy {
      health(-20)
    }

  it should "be hit and lose health" in :
    val h = health(10)
    h.hit(3)
    h.health shouldBe 7
    h.hit(2)
    h.health shouldBe 5

  it should "tell if it's dead" in :
    val h = health(10)
    h.hit(4)
    h.isDead shouldBe false
    h.hit(6)
    h.isDead shouldBe true

  it should "not lose health if already dead" in :
    val h = health(10)
    h.kill()
    h.isDead shouldBe true
    h.health shouldBe 0
    h.hit(3)
    h.isDead shouldBe true
    h.health shouldBe 0

  it should "execute the onHit handler when hit" in :
    var hit = false
    val h = health(10, _onHit = hit = true)
    h.hit(1)
    hit shouldBe true

  it should "execute the onDeath handler when it dies" in :
    var dead = false
    val h = health(1, _onDeath = dead = true)
    h.hit(1)
    dead shouldBe true