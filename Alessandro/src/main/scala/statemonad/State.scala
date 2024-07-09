package statemonad

/** A State monad where the state type can change
  *
  * @param run
  */
case class State[S, SO, O](run: S => (SO, O))

extension [S, SO, O](state: State[S, SO, O])
  def map[O2](f: O => O2): State[S, SO, O2] =
    State(s =>
      val (newState, output) = state.run(s)
      (newState, f(output))
    )

  def flatMap[SO2, O2](f: O => State[SO, SO2, O2]): State[S, SO2, O2] =
    State(s =>
      val (newState, output) = state.run(s)
      f(output).run(newState)
    )
