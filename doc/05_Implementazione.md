# Implementazione

## Scene
Scene ha una implementazione di default attraverso `Scene.apply`.

Il motivo per cui Scene prende in input una funzione che crea i game object e non direttamente i game object è che Scene è solo un template per gli oggetti e questi andranno creati solo quando la scena verrà caricata dall'engine.

## Built-in behaviours

Di seguito sono descritte le implementazioni dei vari Behaviours built-in del SGE.
Da notare che ogni behaviour built-in è un mixin di Behaviour.

### Identifiable

Un oggetto che viene mixato con il behaviour **Identifiable** avrà a disposizione un id e potrà essere cercato attraverso questo tra tutti gli altri oggetti.

### Positionable

Quando un behaviour usa **Positionable** come mixin, avrà accesso ad una x ed una y settate a 0 di default.
Si possono passare i valori iniziali di x e y se non si vuole inizializzarle a 0 e si possono cambiare una volta creato il behaviour.

*Esempio*
```scala
// create a Positionable with x = 5, y = 0 then change y to 3
val positionable: Positionable = new Behaviour() with Positionable(5)
positionable.y = 3
```
### Dimensionable

Un behaviour con **Dimensionable** come mixin ha accesso a due campi, width e height, che rappresentano rispettivamente la larghezza e l'altezza del behaviour.
Il behaviour va inizializzato con entrambi i campi e se sono negativi verranno settati automaticamente a 0, mentre, se una volta che il behaviour è stato inizializzato si provano a cambiare i campi in valori negativi, essi non cambieranno.

*Esempio*
```scala
// create a Dimensionable with width = 5, height = -4 (will be 0 because is negative) 
val dimensionable: Dimensionable = new Behaviour() with Dimensionable(5, -4)
// change height to 3 and try to change width to -4
dimensionable.width = -4
dimensionable.height = 3
println(dimensionable.width) // 5, cannot set width to negative number so it didn't change
println(dimensionable.height) // 3
```