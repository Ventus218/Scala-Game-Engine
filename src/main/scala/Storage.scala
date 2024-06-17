import scala.reflect.TypeTest

trait Storage:
  type Key = String
  def set[T](key: Key, value: T): Unit
  def getOption[T](using
      tt: TypeTest[Any, T]
  )(key: Key): Option[T]
  def unset(key: Key): Unit

import java.{util => ju}
extension (storage: Storage)
  def get[T](using
      tt: TypeTest[Any, T]
  )(key: storage.Key): T = storage.getOption[T](key) match
    case Some(value) => value
    case _ =>
      throw ju.NoSuchElementException(
        s"Element with key: $key not set in storage."
      )

object Storage:
  def apply(): Storage = StorageImmutableMapImpl()

  private class StorageImmutableMapImpl extends Storage:

    private var storageMap: Map[Key, Any] = Map()

    def set[T](key: Key, value: T): Unit =
      storageMap = storageMap updated (key, value)

    def getOption[T](using
        tt: TypeTest[Any, T]
    )(key: Key): Option[T] = storageMap.get(key) match
      case None           => None
      case Some(value: T) => Some(value)
      case Some(value) =>
        throw ClassCastException(
          s"Unable to cast \"$key\" value to the requested data type"
        )

    def unset(key: Key): Unit =
      storageMap = storageMap.removed(key)
