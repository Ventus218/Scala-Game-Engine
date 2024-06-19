# Implementazione

## Storage
Storage permette di salvare coppie chiave valore in memoria volatile.

La natura di questo componente costringe a lavorare con il tipo Any nell'implementazione, comunque cast e controlli riguardanti i tipi vengono effettuati a runtime attraverso reflection.

L'implementazione di default, che viene restituita da `Storage.apply` è `StorageImmutableMapImpl` che sfrutta al suo interno una `Map` immutabile, nel caso si necessiti di migliorare le prestazioni è suggerito creare una nuova implementazione con una struttura dati mutabile.

## Scene
Scene è un type alias per una funzione 0-aria che ritorna un `Iterable[Behaviour]`.
L'utilizzatore del framework può definire le sue scene principalmente in due modi diversi:

**Come object**:
```scala
object BallsScene extends Scene:
    def apply(): Iterable[Behaviour] =
        Seq(
            BallGameObject(
                jumping = true,
                id = "ball_1",
                x = 0,
                y = 0
            ),
            BallGameObject(
                enabled = false,
                jumping = false,
                id = "ball_2",
                x = 10,
                y = 10
            )
        )
// Caricamento di BallsScene
engine.loadScene(BallsScene)
```
**Come def**:
```scala
def ballsScene(): Iterable[Behaviour] =
    Seq(
        BallGameObject(
            jumping = true,
            id = "ball_1",
            x = 0,
            y = 0
        ),
        BallGameObject(
            enabled = false,
            jumping = false,
            id = "ball_2",
            x = 10,
            y = 10
        )
    )
// Caricamento di ballsScene
engine.loadScene(ballsScene)
```
### Motivazioni dietro a questo approccio
Si vuole che Scene sia una funzione in quanto deve essere solo un template per gli oggetti in quanto questi andranno creati solo quando la scena verrà caricata dall'engine.

Inoltre il fatto di definire le scene come `object` o `def` permette all'utente di utilizzare rispettivamente il type system oppure i nomi dei metodi come identificatori delle scene definite.
Se queste vengono poi inserite in un `object` "raccoglitore" permette un utilizzo veramente semplice ed intuitivo:
```scala
object Scenes:
    object MenuScene:
        // ...
    object GameScene:
        // ...

// From the point of view of engine/behaviours:
engine.loadScene(Scenes.MenuScene)
engine.loadScene(Scenes.GameScene)
```

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