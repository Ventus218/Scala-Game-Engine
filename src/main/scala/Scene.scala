trait Scene:
  val objects: () => Iterable[GameObject[?]]
