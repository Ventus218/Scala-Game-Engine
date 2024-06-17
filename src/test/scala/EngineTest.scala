import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.*

class EngineTest extends AnyFlatSpec:
    "Engine" should "be created with IO and Storage" in:
        Engine(io = new IO() {}, storage = new StorageMockup())
    
    private class StorageMockup extends Storage:
      override def set[T](key: String, value: T): Unit = ???

      override def get[T](key: String): T = ???

      override def getOption[T](key: String): Option[T] = ???

      override def unset(key: String): Unit = ???

    
        