# Implementazione

## Storage
Storage permette di salvare coppie chiave valore in memoria volatile.

La natura di questo componente costringe a lavorare con il tipo Any nell'implementazione, comunque cast e controlli riguardanti i tipi vengono effettuati a runtime attraverso reflection.

L'implementazione di default, che viene restituita da `Storage.apply` è `StorageImmutableMapImpl` che sfrutta al suo interno una `Map` immutabile, nel caso si necessiti di migliorare le prestazioni è suggerito creare una nuova implementazione con una struttura dati mutabile.

## Scene
Scene ha una implementazione di default attraverso `Scene.apply`.

Il motivo per cui Scene prende in input una funzione che crea i game object e non direttamente i game object è che Scene è solo un template per gli oggetti e questi andranno creati solo quando la scena verrà caricata dall'engine.

## Built-in behaviours
Di seguito sono descritte le implementazioni dei vari Behaviours built-in del SGE.
Da notare che ogni behaviour built-in è un mixin di Behaviour.

### Identifiable
Un oggetto che viene mixato con il behaviour **Identifiable** avrà a disposizione un `id` e potrà essere cercato attraverso questo tra tutti gli altri oggetti.

### Positionable
Quando un behaviour usa **Positionable** come mixin, avrà accesso ad una `x` ed una `y` settate a 0 di default.
Si possono passare i valori iniziali di `x` e `y` se non si vuole inizializzarle a 0 e si possono cambiare una volta creato il behaviour.

*Esempio*
```scala
// create a Positionable with x = 5, y = 0 then change y to 3
val positionable: Positionable = new Behaviour with Positionable(5)
positionable.y = 3
```
### Dimensionable
Un behaviour con **Dimensionable** come mixin ha accesso a due campi, `width` ed `height`, che rappresentano rispettivamente la larghezza e l'altezza del behaviour.
Il behaviour va inizializzato con entrambi i campi e se sono negativi verranno settati automaticamente a 0, mentre, se una volta che il behaviour è stato inizializzato si provano a cambiare i campi in valori negativi, essi non cambieranno.

*Esempio*
```scala
// create a Dimensionable with width = 5, height = -4 (will be 0 because is negative) 
val dimensionable: Dimensionable = new Behaviour with Dimensionable(5, -4)
// change height to 3 and try to change width to -4
dimensionable.width = -4
dimensionable.height = 3
println(dimensionable.width) // 5, cannot set width to negative number so it didn't change
println(dimensionable.height) // 3
```

### Collider
Un behaviour con **Collider** come mixin dovrà innanzitutto avere in mixin anche **Dimensionable** e **Positionable**. Al **Collider** potranno essere passati due valori, `w` e `h`, rispettivamente la larghezza e l'altezza del **Collider**.
Un valore inferiore o uguale a 0 per `w` o `h` (valori di default dei campi) comporta che larghezza e altezza del **Collider** siano gli stessi del **Dimensionable**.

Il valore dell'altezza e della larghezza potranno poi essere recuperati dall'esterno attraverso i campi `colliderWidth` e `colliderHeight` che non potranno essere cambiati in valori negativi o uguali a 0.

Infine il metodo `collides(other)` accetta in input un **Collider** e torna `true` se si verifica una collisione tra `other` e `this`, altrimenti torna `false`. Per l'algoritmo di collisione si è usato l'algoritmo [AABB collision detection](https://stackoverflow.com/tags/aabb/info).

*Esempio*
```scala
// Creation of a collider with dimension 5x5 at x = 0, y = 0
val collider: Collider = new Behaviour with Collider with Dimensionable(5, 5) with Positionable(0, 0)

// Creation of a collider with dimension 5x5 at x = 4, y = 4
val collider2: Collider = new Behaviour with Collider with Dimensionable(5, 5) with Positionable(4, 4)

// Creation of a collider with dimension 2x2 at x = 10, y = 10
val collider3: Collider = new Behaviour with Collider(2, 2) with Dimensionable(5, 5) with Positionable(6, 6)

println(collider.collides(collider2)) //true
println(collider.collides(collider3)) //false
println(collider2.collides(collider3)) //true
```