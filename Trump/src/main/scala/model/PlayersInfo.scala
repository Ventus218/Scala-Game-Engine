package model

object PlayersInfo:
  opaque type PlayersInfo[PlayerInfo] = PlayersInfoImpl[PlayerInfo]
  private case class PlayersInfoImpl[PlayerInfo](
      val player1: PlayerInfo,
      val player2: PlayerInfo
  )

  extension [PlayerInfo](infos: PlayersInfo[PlayerInfo])
    def player1: PlayerInfo = infos.player1
    def player2: PlayerInfo = infos.player2

  def apply[PlayerInfo](
      player1: PlayerInfo,
      player2: PlayerInfo
  ): Option[PlayersInfo[PlayerInfo]] = player1 == player2 match
    case true  => None
    case false => Some(PlayersInfoImpl(player1 = player1, player2 = player2))
