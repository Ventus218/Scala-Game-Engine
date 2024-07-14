package game.scenes

import sge.core.*
import sge.core.behaviours.dimension2d.Positionable
import sge.swing.*
import game.gameobjects.*
import game.Values
import model.Cards.*

object GameResult extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    MenuCardDecoration(Card(Swords, Ace), (-33, 20), rotation = -30.degrees),
    MenuCardDecoration(Card(Cups, Ace), (-30, 20), rotation = -10.degrees),
    MenuCardDecoration(Card(Clubs, Ace), (33, 20), rotation = 30.degrees),
    MenuCardDecoration(Card(Coins, Ace), (30, 20), rotation = 10.degrees),
    GameTitle(),
    PlayerFinalScore(
      player = Values.Players.p1PlayerName,
      position = (-10, -5)
    ),
    PlayerFinalScore(
      player = Values.Players.p2PlayerName,
      position = (10, -5)
    ),
    ResultMessage(),
    ExitButton(position = (0, -25)),
    new GameButton("Back to menu", (0, -15)):
      override def onButtonPressed: Engine => Unit = _.loadScene(Menu)
  )
