import scala.annotation.targetName
trait IO
trait RendererB extends Behaviour


// Examples 
def getIO(): IO =
  ConsoleIO().asInstanceOf[IO]

def getMyIO(): ConsoleIO = getIO().asInstanceOf[ConsoleIO]

case class ConsoleIO() extends IO:
  import scala.io.StdIn.readLine
  def in(prompt: String = ""): String = readLine(prompt)
  def out(string: String): Unit = println(string)

case class ConsoleIORendererB() extends RendererB:

  override def onUpdate(): Unit =
    getMyIO().out("ciao")
