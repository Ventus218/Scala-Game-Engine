import scala.reflect.TypeTest

/** Is a key-value storage that allows to persist some data in the volatile
  * memory.
  */
trait Storage:
  type Key = String

  /** Adds/overwrites a key with the given value
    *
    * @param key
    * @param value
    */
  def set[T](key: Key, value: T): Unit

  /** Tries to get the value associated to a given key. It also casts the value
    * throwing a ClassCastException if that's not possible.
    *
    * @param tt
    * @param key
    * @return
    */
  def getOption[T](using
      tt: TypeTest[Any, T]
  )(key: Key): Option[T]

  /** Removes a key-value pair (if present) from the storage.
    *
    * @param key
    */
  def unset(key: Key): Unit

import java.{util => ju}
extension (storage: Storage)
  /** Gets the value associated to a given key. It also casts the value throwing
    * a ClassCastException if that's not possible.
    *
    * If no value is associated to the given key a NoSuchElementException is
    * thrown
    */
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
