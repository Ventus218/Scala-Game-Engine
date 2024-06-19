/** A template for the objects that should be instantiated when the engine loads
  * a new scene.
  */
type Scene = () => Iterable[Behaviour]
