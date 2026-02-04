package sge.audio

import sge.core.*

import javax.sound.sampled.*
import java.io.{BufferedInputStream, InputStream}
import java.util.concurrent.atomic.AtomicLong
import scala.collection.concurrent.TrieMap

/** An extension of IO that provides audio playback capabilities.
 */
trait SoundIO extends IO:
  /** The master volume for all audio clips (0.0 to 1.0) */
  def masterVolume: Double

  /** Sets the master volume that affects all audio clips.
   *
   * @param volume the new master volume (0.0 to 1.0)
   */
  def masterVolume_=(volume: Double): Unit
  
  /** Plays an audio clip from the given resource path.
   * @param path
   *   the path to the audio file in the resources folder
   * @param loop
   *   whether the audio should loop continuously
   * @param volume
   *   the volume level (0.0 to 1.0)
   * @return
   *   an AudioClipId that can be used to control the playback
   */
  def play(path: String, loop: Boolean = false, volume: Double = 1.0): AudioClipId

  /** Stops the playback of a specific audio clip.
   * @param clipId
   *   the identifier of the audio clip to stop
   */
  def stop(clipId: AudioClipId): Unit

  /** Pauses the playback of a specific audio clip.
   * @param clipId
   *   the identifier of the audio clip to pause
   */
  def pause(clipId: AudioClipId): Unit

  /** Resumes the playback of a paused audio clip.
   * @param clipId
   *   the identifier of the audio clip to resume
   */
  def resume(clipId: AudioClipId): Unit

  /** Sets the volume for a specific audio clip.
   * @param clipId
   *   the identifier of the audio clip
   * @param volume
   *   the new volume level (0.0 to 1.0)
   */
  def setVolume(clipId: AudioClipId, volume: Double): Unit

  /** Checks if an audio clip is currently playing.
   * @param clipId
   *   the identifier of the audio clip
   * @return
   *   true if the clip is playing, false otherwise
   */
  def isPlaying(clipId: AudioClipId): Boolean

  /** Stops all currently playing audio clips.
   */
  def stopAll(): Unit

/** Represents a unique identifier for an audio clip being played.
 */
opaque type AudioClipId = Long

object AudioClipId:
  /** Creates a new AudioClipId from a Long value.
   * @param id the underlying ID value
   * @return the AudioClipId
   */
  def apply(id: Long): AudioClipId = id

  extension (clipId: AudioClipId)
    /** Gets the underlying Long value of the AudioClipId.
     * @return the ID as a Long
     */
    def value: Long = clipId

  /** Represents an invalid or non-existent audio clip.
   */
  val Invalid: AudioClipId = -1L


/** Companion object for SoundIO with factory methods and implementation.
  */
object SoundIO:

  /** Creates a new SoundIO instance.
    * @param masterVolume the initial master volume (0.0 to 1.0)
    * @return a new SoundIO instance
    */
  def apply(masterVolume: Double = 1.0): SoundIO =
    new SoundIOImpl(masterVolume)

  /** Private implementation of SoundIO.
    */
  private class SoundIOImpl(
      private var _masterVolume: Double
  ) extends SoundIO:

    require(_masterVolume >= 0.0 && _masterVolume <= 1.0, "Master volume must be between 0.0 and 1.0")

    // Thread-safe collections for managing clips
    private val activeClips: TrieMap[AudioClipId, ClipInfo] = TrieMap.empty
    private val clipIdGenerator: AtomicLong = AtomicLong(0)

    // Cached audio data for performance
    private val audioCache: TrieMap[String, Array[Byte]] = TrieMap.empty

    override def masterVolume: Double = _masterVolume
    override def masterVolume_=(volume: Double): Unit =
      require(volume >= 0.0 && volume <= 1.0, "Master volume must be between 0.0 and 1.0")
      _masterVolume = volume
      // Update volume for all active clips
      activeClips.values.foreach(info => applyVolume(info.clip, info.volume))

    override def play(path: String, loop: Boolean = false, volume: Double = 1.0): AudioClipId =
      require(volume >= 0.0 && volume <= 1.0, "Volume must be between 0.0 and 1.0")
      
      try
        val clip = createClip(path)
        val clipId = AudioClipId(clipIdGenerator.incrementAndGet())
        
        applyVolume(clip, volume)
        
        if loop then
          clip.loop(Clip.LOOP_CONTINUOUSLY)
        else
          clip.start()
        
        // Add listener to clean up when clip finishes
        clip.addLineListener(event =>
          if event.getType == LineEvent.Type.STOP && !loop then
            cleanupClip(clipId)
        )
        
        activeClips.put(clipId, ClipInfo(clip, volume, loop))
        clipId
      catch
        case e: Exception =>
          System.err.println(s"Error playing audio: ${e.getMessage}")
          AudioClipId.Invalid

    override def stop(clipId: AudioClipId): Unit =
      activeClips.get(clipId).foreach { info =>
        info.clip.stop()
        info.clip.close()
        activeClips.remove(clipId)
      }

    override def pause(clipId: AudioClipId): Unit =
      activeClips.get(clipId).foreach { info =>
        if info.clip.isRunning then
          info.clip.stop()
      }

    override def resume(clipId: AudioClipId): Unit =
      activeClips.get(clipId).foreach { info =>
        if !info.clip.isRunning then
          if info.looping then
            info.clip.loop(Clip.LOOP_CONTINUOUSLY)
          else
            info.clip.start()
      }

    override def setVolume(clipId: AudioClipId, volume: Double): Unit =
      require(volume >= 0.0 && volume <= 1.0, "Volume must be between 0.0 and 1.0")
      activeClips.get(clipId).foreach { info =>
        activeClips.put(clipId, info.copy(volume = volume))
        applyVolume(info.clip, volume)
      }

    override def isPlaying(clipId: AudioClipId): Boolean =
      activeClips.get(clipId).exists(_.clip.isRunning)

    override def stopAll(): Unit =
      activeClips.keys.foreach(stop)

    override def onEngineStop(): Unit =
      stopAll()
      audioCache.clear()

    private def createClip(path: String): Clip =
      // Try to get cached audio data or load it
      val audioData = audioCache.getOrElseUpdate(path, loadAudioData(path))
      
      val inputStream = AudioSystem.getAudioInputStream(
        new BufferedInputStream(new java.io.ByteArrayInputStream(audioData))
      )
      val clip = AudioSystem.getClip()
      clip.open(inputStream)
      clip

    private def loadAudioData(path: String): Array[Byte] =
      val resourceStream: InputStream = getClass.getResourceAsStream(s"/$path")
      if resourceStream == null then
        throw IllegalArgumentException(s"Audio resource not found: $path")
      
      val buffered = new BufferedInputStream(resourceStream)
      val data = buffered.readAllBytes()
      buffered.close()
      data

    private def applyVolume(clip: Clip, clipVolume: Double): Unit =
      if clip.isControlSupported(FloatControl.Type.MASTER_GAIN) then
        val gainControl = clip.getControl(FloatControl.Type.MASTER_GAIN).asInstanceOf[FloatControl]
        val effectiveVolume = clipVolume * _masterVolume
        // Convert linear volume (0.0 - 1.0) to decibels
        val dB = if effectiveVolume > 0 then
          20.0f * Math.log10(effectiveVolume).toFloat
        else
          gainControl.getMinimum
        
        // Clamp to valid range
        val clampedDb = Math.max(gainControl.getMinimum, Math.min(gainControl.getMaximum, dB))
        gainControl.setValue(clampedDb)

    private def cleanupClip(clipId: AudioClipId): Unit =
      activeClips.get(clipId).foreach { info =>
        info.clip.close()
        activeClips.remove(clipId)
      }

    /** Internal case class to track clip information */
    private case class ClipInfo(
        clip: Clip,
        volume: Double,
        looping: Boolean
    )

  /** Builder for SoundIO instances.
    * @param masterVolume the master volume (0.0 to 1.0)
    */
  case class SoundIOBuilder(
      masterVolume: Double = 1.0
  )

  /** Build a SoundIO with a new master volume.
    * @param volume the new master volume
    * @return a new builder
    */
  def withMasterVolume(volume: Double): SoundIOBuilder =
    SoundIOBuilder(masterVolume = volume)

  extension (builder: SoundIOBuilder)
    /** Create a new SoundIO from this builder configuration.
      * @return a SoundIO implementation
      */
    def build(): SoundIO = SoundIO(builder.masterVolume)

    /** Build a SoundIO with a new master volume.
      * @param volume the new master volume
      * @return a new builder
      */
    def withMasterVolume(volume: Double): SoundIOBuilder =
      builder.copy(masterVolume = volume)
