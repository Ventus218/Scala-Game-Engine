import sge.core.*
import sge.swing.*

@main def main =
  val io = SwingIO
    .withTitle("Stealth Game")
    .withSize(1200, 720)
    .withPixelsPerUnitRatio(5)
    .build()

  Engine(io, Storage()).run(MenuScene)



object MenuScene extends Scene:
  override def apply(): Iterable[Behaviour] = Seq(

  )