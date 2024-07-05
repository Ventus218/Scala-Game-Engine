import SwingRenderers.SwingRectRenderer
import SwingIO.*
import SwingInputHandler.{*, given}

trait SwingButton(
    var buttonText: String = "",
    var inputButtonTriggers: Set[InputButton] = Set(InputButton.MouseButton1)
) extends Behaviour
    with SwingRectRenderer
    with SwingInputHandler:

  var inputHandlers: Map[InputButton, Handler] =
    /* Apparently needs to be explicit about the type to let the conversion work */
    val tuples: Set[(InputButton, Handler)] =
      inputButtonTriggers.map(_ -> onButtonPressed.onlyWhenReleased)
    Map(tuples.toSeq*)

  def onButtonPressed: Engine => Unit = _ => ()
