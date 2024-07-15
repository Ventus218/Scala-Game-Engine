package model

import Cards.*

object Field:
  opaque type Field[PlayerInfo] = Seq[PlacedCard[PlayerInfo]]

  extension [PlayerInfo](field: Field[PlayerInfo])
    def size: Int = field.size

    def place(card: Card, player: PlayerInfo): Field[PlayerInfo] =
      field :+ PlacedCard(card, player)

    def placedCards: Seq[PlacedCard[PlayerInfo]] = field

  def apply[PlayerInfo](): Field[PlayerInfo] = Seq()

  case class PlacedCard[PlayerInfo](card: Card, playerInfo: PlayerInfo)
