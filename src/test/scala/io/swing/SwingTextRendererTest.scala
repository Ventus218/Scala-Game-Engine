import SwingRendererTestUtilities.*
import SwingRenderers.*
import org.scalatest.flatspec.AnyFlatSpec

import java.awt.Color

object SwingTextRendererTest:

  @main def testSwingRendererText(): Unit =
    testSwingRenderer:
      textRenderer("Hello World!", 20, Color.blue)

  @main def testSwingRendererTextPlacement(): Unit =
    testSwingRendererPlacement(
      centered = textRenderer(
        "Hello World!",
        20,
        Color.red,
        position = (200, 200)
      ),
      topLeft = textRenderer(
        "Ciao Mondo!",
        14,
        Color.blue,
        position = (0, 0)
      ),
      topRight = textRenderer(
        "Hola Mundo!",
        17,
        Color.green,
        position = (400, 0)
      )
    )
