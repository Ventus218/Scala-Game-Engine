object BehaviourUtils:
  import scala.reflect.TypeTest
  import Behaviours.*

  extension [T <: Behaviour](iterable: Iterable[T])
    def filter[B <: Behaviour](using
        tt: TypeTest[T, B]
    )(): Iterable[B] =
      iterable
        .filter(_ match
          case tt(_) => true
          case _     => false
        )
        .map(_.asInstanceOf[B])

    def find[B <: Identifiable](using
        tt: TypeTest[T, B]
    )(id: String): Option[B] =
      iterable
        .filter[B]()
        .find(_.id == id)
