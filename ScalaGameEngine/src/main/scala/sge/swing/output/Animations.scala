package sge.swing.output

import java.awt.{Graphics2D, Image as AWTImage}
import Images.{ImageLoader, ImageResizer}
import GameElements.*

/** Utility object for animations */
object Animations:

  /** Represents a single frame of an animation.
    * @param imagePath
    *   the path to the image file in resources
    * @param duration
    *   the duration of this frame in seconds
    */
  case class AnimationFrame(imagePath: String, duration: Double):
    require(duration > 0, "Frame duration must be positive")
    val image: ImageResizer = ImageResizer(imagePath)

  /** Represents an animation as a sequence of frames.
    * @param frames
    *   the sequence of animation frames
    * @param loop
    *   whether the animation should loop when it reaches the end
    */
  class Animation(
      val frames: Seq[AnimationFrame],
      val loop: Boolean = true
  ):
    require(frames.nonEmpty, "Animation must have at least one frame")

    private var _currentFrameIndex: Int = 0
    private var _elapsedTime: Double = 0.0
    private var _finished: Boolean = false

    /** The index of the current frame being displayed. */
    def currentFrameIndex: Int = _currentFrameIndex

    /** The current frame being displayed. */
    def currentFrame: AnimationFrame = frames(_currentFrameIndex)

    /** Whether the animation has finished playing (only relevant for non-looping animations). */
    def isFinished: Boolean = _finished

    /** The total duration of the animation in seconds. */
    def totalDuration: Double = frames.map(_.duration).sum

    /** Updates the animation state based on elapsed time.
      * @param deltaTimeSeconds
      *   the time elapsed since the last update in seconds
      */
    def update(deltaTimeSeconds: Double): Unit =
      if _finished then return

      _elapsedTime += deltaTimeSeconds

      while _elapsedTime >= currentFrame.duration do
        _elapsedTime -= currentFrame.duration
        
        if _currentFrameIndex < frames.length - 1 then
          _currentFrameIndex += 1
        else if loop then
          _currentFrameIndex = 0
        else
          _finished = true
          _elapsedTime = 0

    /** Resets the animation to its initial state. */
    def reset(): Unit =
      _currentFrameIndex = 0
      _elapsedTime = 0.0
      _finished = false

    /** Jumps to a specific frame index.
      * @param index
      *   the frame index to jump to
      */
    def goToFrame(index: Int): Unit =
      require(index >= 0 && index < frames.length, s"Frame index must be between 0 and ${frames.length - 1}")
      _currentFrameIndex = index
      _elapsedTime = 0.0
      _finished = false

    /** Creates a copy of this animation with independent state. */
    def copy(): Animation =
      val newAnim = new Animation(frames, loop)
      newAnim._currentFrameIndex = this._currentFrameIndex
      newAnim._elapsedTime = this._elapsedTime
      newAnim._finished = this._finished
      newAnim

  object Animation:
    /** Creates an animation from a list of image paths with uniform frame duration.
      * @param imagePaths
      *   the paths to the frame images
      * @param frameDuration
      *   the duration of each frame in seconds
      * @param loop
      *   whether the animation should loop (default true)
      * @return
      *   a new Animation
      */
    def uniform(imagePaths: Seq[String], frameDuration: Double, loop: Boolean = true): Animation =
      new Animation(imagePaths.map(path => AnimationFrame(path, frameDuration)), loop)

    /** Creates an animation from frame image paths with a naming pattern.
      * @param basePath
      *   the base path without extension (e.g., "sprites/walk")
      * @param extension
      *   the file extension (e.g., "png")
      * @param frameCount
      *   the number of frames
      * @param frameDuration
      *   the duration of each frame in seconds
      * @param loop
      *   whether the animation should loop (default true)
      * @param startIndex
      *   the starting index for frame numbering (default 0)
      * @return
      *   a new Animation
      *
      * This will look for files like: sprites/walk_0.png, sprites/walk_1.png, etc.
      */
    def fromPattern(
        basePath: String,
        extension: String,
        frameCount: Int,
        frameDuration: Double,
        loop: Boolean = true,
        startIndex: Int = 0
    ): Animation =
      val paths = (startIndex until (startIndex + frameCount))
        .map(i => s"${basePath}_$i.$extension")
      uniform(paths, frameDuration, loop)

  /** A GameElement that displays an animated image.
    */
  trait AnimatedImage extends GameElement:
    /** The animation being played. */
    val animation: Animation

    override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
      g2d =>
        (posX, posY, w, h) =>
          val img = animation.currentFrame.image.resize(w, h)
          g2d.drawImage(img, posX, posY, null)

  private class SimpleAnimatedImage(
      override val animation: Animation,
      width: Double,
      height: Double
  ) extends BaseGameElement(width, height)
      with AnimatedImage

  /** Creates an AnimatedImage from an Animation.
    * @param animation
    *   the animation to display
    * @param width
    *   the width in game units
    * @param height
    *   the height in game units
    * @return
    *   a new AnimatedImage
    */
  def animatedImage(
      animation: Animation,
      width: Double,
      height: Double
  ): AnimatedImage =
    SimpleAnimatedImage(animation, width, height)

  /** Manages multiple named animations with easy switching.
    * Useful for characters with different states (idle, walk, run, etc.)
    */
  class AnimationController(
      private val animations: Map[String, Animation],
      private val defaultAnimation: String,
      width: Double,
      height: Double
  ) extends BaseGameElement(width, height):
    require(animations.nonEmpty, "Must have at least one animation")
    require(animations.contains(defaultAnimation), s"Default animation '$defaultAnimation' not found")

    private var _currentAnimationName: String = defaultAnimation
    private var _currentAnimation: Animation = animations(defaultAnimation).copy()

    /** The name of the currently playing animation. */
    def currentAnimationName: String = _currentAnimationName

    /** The currently playing animation. */
    def currentAnimation: Animation = _currentAnimation

    /** Updates the current animation.
      * @param deltaTimeSeconds
      *   the time elapsed since the last update
      */
    def update(deltaTimeSeconds: Double): Unit =
      _currentAnimation.update(deltaTimeSeconds)

    /** Switches to a different animation.
      * @param name
      *   the name of the animation to switch to
      * @param resetIfSame
      *   whether to reset if switching to the same animation (default false)
      */
    def play(name: String, resetIfSame: Boolean = false): Unit =
      if !animations.contains(name) then
        throw IllegalArgumentException(s"Animation '$name' not found")
      
      if name != _currentAnimationName || resetIfSame then
        _currentAnimationName = name
        _currentAnimation = animations(name).copy()

    /** Resets the current animation to its beginning. */
    def reset(): Unit =
      _currentAnimation.reset()

    /** Checks if a specific animation exists.
      * @param name
      *   the animation name
      * @return
      *   true if the animation exists
      */
    def hasAnimation(name: String): Boolean =
      animations.contains(name)

    override def drawElement: Graphics2D => (Int, Int, Int, Int) => Unit =
      g2d => (x, y, w, h) =>
        val img = this.currentAnimation.currentFrame.image.resize(w, h)
        g2d.drawImage(img, x, y, null)

  object AnimationController:
    /** Builder for creating an AnimationController.
      */
    class AnimationControllerBuilder:
      private var animations: Map[String, Animation] = Map.empty
      private var defaultAnim: Option[String] = None
      private var width: Double = 1.0
      private var height: Double = 1.0

      /** Adds an animation to the controller.
        * @param name
        *   the name for this animation
        * @param animation
        *   the animation
        * @return
        *   this builder
        */
      def addAnimation(name: String, animation: Animation): AnimationControllerBuilder =
        animations = animations + (name -> animation)
        this

      /** Sets the default animation.
        * @param name
        *   the name of the default animation
        * @return
        *   this builder
        */
      def withDefault(name: String): AnimationControllerBuilder =
        defaultAnim = Some(name)
        this

      /**
        * Sets the size of the animation controller.
        *
        * @param width
        * @param height
        * @return
        */
      def withSize(width: Double, height: Double): AnimationControllerBuilder =
        this.width = width
        this.height = height
        this

      /** Builds the AnimationController.
        * @return
        *   a new AnimationController
        */
      private[swing] def build(): AnimationController =
        new AnimationController(animations, defaultAnim.getOrElse(
          throw IllegalStateException("Default animation must be set")
        ), width, height)

    /** Creates a new builder for AnimationController. */
    def builder(): AnimationControllerBuilder = new AnimationControllerBuilder()
