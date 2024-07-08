package sge.core.behaviours

import sge.core.Behaviour

/** Add an id to a Behaviour so that the Behaviour can be found by that id
  *
  * @param id
  *   id of the Behaviour
  */
trait Identifiable(val id: String) extends Behaviour
