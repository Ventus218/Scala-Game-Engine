trait SceneManager:
  type SceneName = String

  val scenes: Map[SceneName, Scene]

  def addScene(sceneName: SceneName, scene: Scene): SceneManager
  def addScenes(tuples: (SceneName, Scene)*): SceneManager
