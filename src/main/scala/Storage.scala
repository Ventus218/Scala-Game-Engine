trait Storage:
    type Key = String
    def set[T](key: Key, value: T): Unit
    def get[T](key: Key): T
    def getOption[T](key: Key): Option[T]
    def unset(key: Key): Unit
