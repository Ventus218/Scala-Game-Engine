package game.scenes

import sge.core.*
import game.gameobjects.*
import model.Cards.*
import sge.core.behaviours.dimension2d.Positionable
import sge.swing.behaviours.ingame.TextRenderer
import java.awt.Color

object Menu extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(
    PlayButton(position = (0, -5)),
    PlaySmallDeckButton(position = (0, -15)),
    ExitButton(position = (0, -25)),
    MenuCardDecoration(Card(Swords, Ace), (-33, 20), rotation = -30.degrees),
    MenuCardDecoration(Card(Cups, Ace), (-30, 20), rotation = -10.degrees),
    MenuCardDecoration(Card(Clubs, Ace), (33, 20), rotation = 30.degrees),
    MenuCardDecoration(Card(Coins, Ace), (30, 20), rotation = 10.degrees),
    new Behaviour
      with Positionable(Versor2D.up * 20)
      with TextRenderer("Trump!", size = 10, Color.black)
  )
