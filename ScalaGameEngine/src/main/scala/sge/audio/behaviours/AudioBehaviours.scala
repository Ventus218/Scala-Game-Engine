package sge.audio.behaviours

import sge.core.*
import sge.audio.*

/** Base behaviour for playing audio. It provides the basic mechanism to 
  * interact with SoundIO for audio playback. Similar to how Renderer
  * interacts with SwingIO for graphics.
  */
trait AudioPlayer extends Behaviour:
  /** The current audio clip ID being played by this behaviour.
    * Returns AudioClipId.Invalid if no clip is playing.
    */
  protected var currentClipId: AudioClipId = AudioClipId.Invalid
  
  /** Plays the given audio path through the engine's SoundIO.
    * @param engine the game engine
    * @param path the path to the audio file
    * @param loop whether the audio should loop
    * @param volume the volume level (0.0 to 1.0)
    */
  protected def playAudio(engine: Engine, path: String, loop: Boolean = false, volume: Double = 1.0): Unit =
    val soundIO = engine.io.asInstanceOf[SoundIO]
    currentClipId = soundIO.play(path, loop, volume)

  /** Stops the currently playing audio clip.
    * @param engine the game engine
    */
  protected def stopAudio(engine: Engine): Unit =
    if currentClipId != AudioClipId.Invalid then
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.stop(currentClipId)
      currentClipId = AudioClipId.Invalid

  /** Pauses the currently playing audio clip.
    * @param engine the game engine
    */
  protected def pauseAudio(engine: Engine): Unit =
    if currentClipId != AudioClipId.Invalid then
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.pause(currentClipId)

  /** Resumes the currently paused audio clip.
    * @param engine the game engine
    */
  protected def resumeAudio(engine: Engine): Unit =
    if currentClipId != AudioClipId.Invalid then
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.resume(currentClipId)

  /** Sets the volume for the currently playing audio clip.
    * @param engine the game engine
    * @param volume the new volume level (0.0 to 1.0)
    */
  protected def setAudioVolume(engine: Engine, volume: Double): Unit =
    if currentClipId != AudioClipId.Invalid then
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.setVolume(currentClipId, volume)

  /** Checks if the current audio clip is playing.
    * @param engine the game engine
    * @return true if audio is playing, false otherwise
    */
  protected def isAudioPlaying(engine: Engine): Boolean =
    if currentClipId != AudioClipId.Invalid then
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.isPlaying(currentClipId)
    else
      false

/** A behaviour that plays a sound effect once when enabled.
  * Useful for one-shot sounds like explosions, pickups, etc.
  *
  * @param audioPath the path to the audio file in resources
  * @param volume the volume level (0.0 to 1.0)
  */
trait SoundEffect(
    audioPath: String,
    volume: Double = 1.0
) extends AudioPlayer:
  
  override def onStart: Engine => Unit =
    engine =>
      super.onStart(engine)
      playAudio(engine, audioPath, loop = false, volume)

  override def onDeinit: Engine => Unit =
    engine =>
      super.onDeinit(engine)
      stopAudio(engine)

/** A behaviour that plays background music in a loop.
  * The music starts when the object is enabled and stops when disabled.
  *
  * @param musicPath the path to the audio file in resources
  * @param volume the volume level (0.0 to 1.0)
  */
trait BackgroundMusic(
    musicPath: String,
    volume: Double = 1.0
) extends AudioPlayer:
  
  private var musicVolume: Double = volume

  /** Gets the current music volume. */
  def getMusicVolume: Double = musicVolume

  /** Sets the music volume.
    * @param newVolume the new volume level (0.0 to 1.0)
    * @param engine optional engine reference for live update
    */
  def setMusicVolume(newVolume: Double)(using engine: Option[Engine] = None): Unit =
    musicVolume = newVolume
    engine.foreach(e => setAudioVolume(e, newVolume))

  override def onStart: Engine => Unit =
    engine =>
      super.onStart(engine)
      playAudio(engine, musicPath, loop = true, musicVolume)

  override def onEnabled: Engine => Unit =
    engine =>
      super.onEnabled(engine)
      if currentClipId != AudioClipId.Invalid then
        resumeAudio(engine)

  override def onDisabled: Engine => Unit =
    engine =>
      super.onDisabled(engine)
      pauseAudio(engine)

  override def onDeinit: Engine => Unit =
    engine =>
      super.onDeinit(engine)
      stopAudio(engine)

/** A behaviour that allows playing sounds on demand via the playSound method.
  * Useful when you need to trigger sounds from game logic.
  */
trait SoundEmitter extends AudioPlayer:
  
  private var lastEngine: Option[Engine] = None

  override def onUpdate: Engine => Unit =
    engine =>
      super.onUpdate(engine)
      lastEngine = Some(engine)

  /** Plays a sound effect once.
    * @param path the path to the audio file
    * @param volume the volume level (0.0 to 1.0)
    */
  def playSound(path: String, volume: Double = 1.0): Unit =
    lastEngine.foreach { engine =>
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.play(path, loop = false, volume)
    }

  /** Plays a looping sound.
    * @param path the path to the audio file
    * @param volume the volume level (0.0 to 1.0)
    * @return the AudioClipId of the playing clip
    */
  def playLoopingSound(path: String, volume: Double = 1.0): AudioClipId =
    lastEngine.map { engine =>
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.play(path, loop = true, volume)
    }.getOrElse(AudioClipId.Invalid)

  /** Stops a specific sound by its clip ID.
    * @param clipId the ID of the clip to stop
    */
  def stopSound(clipId: AudioClipId): Unit =
    lastEngine.foreach { engine =>
      val soundIO = engine.io.asInstanceOf[SoundIO]
      soundIO.stop(clipId)
    }

  override def onDeinit: Engine => Unit =
    engine =>
      super.onDeinit(engine)
      stopAudio(engine)
