package model

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class PlayersInfoTests extends AnyFlatSpec:
  val playersInfo = PlayersInfo(player1 = "P1", player2 = "P2").get

  "PlayersInfo" should "contain two players info" in:
    playersInfo.player1 == "P1"
    playersInfo.player2 == "P2"

  it should "accept only info that are not equal" in:
    PlayersInfo(player1 = "P1", player2 = "P1") shouldBe None

  it should "accept more complex player info" in:
    case class PlayerInfo(id: Int, name: String)
    val p1 = PlayerInfo(1, "Tizio")
    val p2 = PlayerInfo(2, "Caio")
    PlayersInfo(player1 = p1, player2 = p2) match
      case Some(infos) => (infos.player1, infos.player2) shouldBe (p1, p2)
