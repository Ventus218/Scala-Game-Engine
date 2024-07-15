package statemonad

/** A State monad where a failing operation determines the failure of the whole
  * computation.
  *
  * The state type can evolve during the computation.
  */
case class EitherState[S, SO, O, E](run: S => Either[E, (SO, O)])

extension [S, SO, O, E](state: EitherState[S, SO, O, E])
  def map[O2](f: O => O2): EitherState[S, SO, O2, E] =
    EitherState(s =>
      state.run(s) match
        case Right(newState, output) => Right(newState, f(output))
        case Left(err)               => Left(err)
    )

  def flatMap[SO2, O2](
      f: O => EitherState[SO, SO2, O2, E]
  ): EitherState[S, SO2, O2, E] =
    EitherState(s =>
      state.run(s) match
        case Right(newState, output) => f(output).run(newState)
        case Left(err)               => Left(err)
    )
