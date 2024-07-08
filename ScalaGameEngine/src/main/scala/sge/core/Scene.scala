package sge.core

/** A template for the objects that should be instantiated when the engine loads
  * a new scene.
  */
type Scene = () => Iterable[Behaviour]

extension (s: Scene) def joined(scene: Scene): Scene = () => s() ++ scene()
