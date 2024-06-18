/** A template for the objects that should be instantiated when the engine loads
  * a new scene.
  */
trait Scene:
  val gameObjects: () => Iterable[Behaviour]

object Scene:
  private case class SceneImpl(gameObjects: () => Iterable[Behaviour])
      extends Scene

  def apply(gameObjects: () => Iterable[Behaviour]): Scene =
    SceneImpl(gameObjects)
