trait Scene:
  val gameObjects: () => Iterable[GameObject[?]]
