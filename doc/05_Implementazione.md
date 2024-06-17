# Implementazione

## Context
Context offre un'implementazione di default tramite `Context.apply` che non è altro che una case class.

Inoltre mette a disposizione dei metodi di utilità (come riferimenti diretti ad IO, Storage e deltaTimeNanos la conversione di quest'ultimo in secondi.) che possono anche essere aggiunti dall'utente finale attraverso delle *extensions*.

## Built-in behaviours

Di seguito sono descritte le implementazioni dei vari Behaviours built-in del SGE.
Da notare che ogni behaviour built-in è un mixin di Behaviour.

### PositionB

Quando un behaviour usa PositionB come mixin, avrà accesso ad una x ed una y settate a 0 di default.
Si possono passare i valori iniziali di x e y se non si vuole inizializzarle a 0 e si possono cambiare una volta creato il behaviour.

*Esempio*
```scala
// create a PositionB with x = 5, y = 0 then change y to 3
val positionB: PositionB = new Behaviour() with PositionB(5)
positionB.y = 3
```
