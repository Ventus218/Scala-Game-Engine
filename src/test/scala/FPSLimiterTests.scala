import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class FPSLimiterTests extends AnyFlatSpec:
  val fpsLimit = 60
  def newLimiter(fpsLimit: Int = fpsLimit): FPSLimiter = FPSLimiter(fpsLimit)

  "FPSLimiter" should "sleep for the right amount of time" in:
    val limiter = newLimiter()

    val start = System.currentTimeMillis()
    for i <- 0 until fpsLimit do
      val startFrame = System.nanoTime()
      limiter.sleepToRespectFPSLimit(startFrame)
      limiter.onFrameEnd()

    val elapsedSeconds = (System.currentTimeMillis() - start) / 1_000d
    val expectedSeconds = 1d

    (elapsedSeconds - expectedSeconds).abs should be <= 0.1

  it should "allow to change the fps limit" in:
    val limiter = newLimiter()
    val newFpsLimit = 30

    val start = System.currentTimeMillis()
    for i <- 0 until fpsLimit do

      if i == fpsLimit / 2 then limiter.fpsLimit = newFpsLimit

      val startFrame = System.nanoTime()
      limiter.sleepToRespectFPSLimit(startFrame)
      limiter.onFrameEnd()

    val elapsedSeconds = (System.currentTimeMillis() - start) / 1_000d
    val expectedSeconds = 1.5d

    (elapsedSeconds - expectedSeconds).abs should be <= 0.1

  it should "set new fps limit only after a frame is completed"
  val limiter = newLimiter()
  val newFpsLimit = 30

  limiter.fpsLimit shouldBe fpsLimit
  limiter.fpsLimit = newFpsLimit
  limiter.fpsLimit shouldBe fpsLimit

  limiter.onFrameEnd()
  limiter.fpsLimit shouldBe newFpsLimit
