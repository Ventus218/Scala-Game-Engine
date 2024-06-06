trait SceneManager:
  type SceneName = String
  
  val scenes: Map[SceneName, Scene]
