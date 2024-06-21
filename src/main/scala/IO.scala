/**
 * The interface of the game engine. Depending on its implementation, it should
 * show the current state of the game loop and the game objects in the active scene.
 * It can be used by the Behaviour classes to update their own visual representation.
 */
trait IO:
  /**
   * This method is called by the game engine to notify the end of the current frame.
   * It defines the operation to apply, and it accepts the Engine class.
   * @return the operation.
   */
  def onFrameEnd: Engine => Unit = (_) => {}
