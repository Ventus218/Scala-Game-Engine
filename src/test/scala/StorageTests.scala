import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*
import org.scalatest.BeforeAndAfterEach
import java.{util => ju}

class StorageTests extends AnyFlatSpec with BeforeAndAfterEach:
  var storage: Storage = Storage()
  val playerNameKey = "player_name"
  val playerNameValue = "Frank"

  override protected def beforeEach(): Unit =
    storage = Storage()

  "Storage" should "return empty optional when trying to get an unset value" in:
    storage.getOption[String](playerNameKey) shouldBe None

  it should "throw when trying to get an unset value" in:
    assertThrows[ju.NoSuchElementException]:
      storage.get[String](playerNameKey)

  it should "persist key-value pairs" in:
    storage.set(playerNameKey, playerNameValue)
    storage.get[String](playerNameKey) shouldBe playerNameValue

  it should "throw when trying to get a value of the wrong data type" in:
    storage.set("one", 1)
    assertThrows[ClassCastException]:
      storage.get[String]("one") shouldBe "1"

  it should "allow to change a value" in:
    storage.set(playerNameKey, playerNameValue)
    val newValue = "changed"
    storage.set(playerNameKey, newValue)
    storage.get[String](playerNameKey) shouldBe newValue

  it should "allow to unset a key-value pair" in:
    storage.set(playerNameKey, playerNameValue)
    storage.unset(playerNameKey)
    storage.getOption[String](playerNameKey) shouldBe None

  it should "do nothing when trying to unset a key with is already not set" in:
    storage.unset(playerNameKey)
    storage.getOption[String](playerNameKey) shouldBe None
