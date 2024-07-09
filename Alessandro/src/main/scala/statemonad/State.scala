package statemonad

case class State[S, O](run: S => (S, O))

extension [S, O](state: State[S, O])
  def map[O2](f: O => O2): State[S, O2] =
    State(s =>
      val (newState, output) = state.run(s)
      (newState, f(output))
    )

  def flatMap[O2](f: O => State[S, O2]): State[S, O2] =
    State(s =>
      val (newState, output) = state.run(s)
      f(output).run(newState)
    )
