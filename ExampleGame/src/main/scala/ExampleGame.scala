import sge.core.*
import sge.swing.*

object ExampleGame extends App:
  val e = Engine(
    SwingIO.withTitle("ExampleGame").withSize(400, 400).build(),
    Storage(),
    60
  ).run(() => Seq())
  println("ExampleGame")
