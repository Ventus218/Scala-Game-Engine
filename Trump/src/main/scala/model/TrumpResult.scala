package model

enum TrumpResult[+PlayerInfo]:
  case Win(val playerInfo: PlayerInfo)
  case Draw extends TrumpResult[Nothing]
