package game

import model.Trump.*
import model.TrumpResult

case class GameResult(
    game: Game[String],
    result: TrumpResult[String]
)
