package model

enum TrumpResult:
  case Win[PlayerInfo](val playerInfo: PlayerInfo)
  case Draw
