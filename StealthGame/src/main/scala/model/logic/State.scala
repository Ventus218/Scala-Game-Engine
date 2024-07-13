package model.logic

/** Rappresents how a state S evolves with a function passed as input
  *
  * @param run
  *   function that evolve S producing a result A
  */
case class State[S, A](run: S => (S, A))

object State:
  extension [S, A](m: State[S, A])
    def apply(s: S): (S, A) = m match
      case State(run) => run(s)

/** Monad trait
  */
trait Monad[M[_]]:
  def unit[A](a: A): M[A]
  extension [A](m: M[A])
    def flatMap[B](f: A => M[B]): M[B]
    def map[B](f: A => B): M[B] = m.flatMap(a => unit(f(a)))

given stateMonad[S]: Monad[[A] =>> State[S, A]] with
  override def unit[A](a: A): State[S, A] = State(s => (s, a))

  extension [A](m: State[S, A])
    override def flatMap[B](f: A => State[S, B]): State[S, B] =
      State(s =>
        m(s) match
          case (s2, a) => f(a)(s2)
      )
