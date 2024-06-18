trait Scene:
  val gameObjects: () => Iterable[Behaviour]
