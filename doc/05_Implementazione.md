# Implementazione

## Scene
Scene ha una implementazione di default attraverso `Scene.apply`.

Il motivo per cui Scene prende in input una funzione che crea i game object e non direttamente i game object è che Scene è solo un template per gli oggetti e questi andranno creati solo quando la scena verrà caricata dall'engine.

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
