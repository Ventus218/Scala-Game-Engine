package model.logic

case class NextState[S, A](run: S => (S, A))

object NextState:
  extension [S, A](m: NextState[S, A])
    def apply(s: S): (S, A) = m match
      case NextState(run) => run(s)

trait Monad[M[_]]:
  def unit[A](a: A): M[A]
  extension [A](m: M[A])
    def flatMap[B](f: A => M[B]): M[B]
    def map[B](f: A => B): M[B] = m.flatMap(a => unit(f(a)))

given stateMonad[S]: Monad[[A] =>> NextState[S, A]] with
  override def unit[A](a: A): NextState[S, A] = NextState(s => (s, a))

  extension [A](m: NextState[S, A])
    override def flatMap[B](f: A => NextState[S, B]): NextState[S, B] =
      NextState(s =>
        m(s) match
          case (s2, a) => f(a)(s2)
      )
