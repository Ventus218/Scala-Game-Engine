trait SceneManager:
  type SceneName = String

  val scenes: Map[SceneName, Scene]

  extension (sm: SceneManager)
    def addScene(sceneName: SceneName, scene: Scene): SceneManager
    def addScenes(tuples: (SceneName, Scene)*): SceneManager =
      tuples.headOption
        .map(tuple =>
          val (sceneName, scene) = tuple
          sm.addScene(sceneName, scene).addScenes(tuples.drop(1)*)
        )
        .getOrElse(sm)
