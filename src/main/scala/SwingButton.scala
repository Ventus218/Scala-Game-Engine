import SwingRenderers.SwingRectRenderer
import SwingIO.InputButton

trait SwingButton(
    var buttonText: String = "",
    var inputButtonTriggers: Set[InputButton] = Set(InputButton.MouseButton1)
) extends Behaviour
    with SwingRectRenderer:
  def onButtonPressed: Engine => Unit = _ => ()
