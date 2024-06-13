import scala.annotation.targetName
trait IO
trait RendererB extends Behaviour

extension (context: Context)
  def consoleIO(): ConsoleIO =
    context.io.asInstanceOf[ConsoleIO]

case class ConsoleIO() extends IO:
  import scala.io.StdIn.readLine
  def in(prompt: String = ""): String = readLine(prompt)
  def out(string: String): Unit = println(string)

trait ConsoleIORendererB() extends RendererB:

  override def onUpdate: Context => Unit =
    (context) =>
      context.consoleIO().out(s"ciao il mio id è: ${context.gameObject.id}")
