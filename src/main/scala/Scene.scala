trait Scene:
  val objects: Unit => Iterable[GameObject[?]]
