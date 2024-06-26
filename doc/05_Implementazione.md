# Implementazione

## Engine
L'engine ha un'implementazione di default attraverso `Engine.apply()` che accetta come parametri una IO e uno Storage.

### Game Loop (run() e stop())
Una volta avviato il game loop attraverso il metodo `engine.run()`, il game loop per prima cosa chiamerà gli handler dei behaviors nel seguente ordine:

    - onInit
    - onEnabled (solo sui behaviours abilitati)
    - onStart (solo sui behaviours abilitati)
    Loop until stopped
        - onEarlyUpdate (solo sui behaviours abilitati)
        - onUpdate (solo sui behaviours abilitati)
        - onLateUpdate (solo sui behaviours abilitati)
    -onDeinit

Chiamando il metodo `engine.stop()` l'engine capirà che si deve fermare ed una volta finito l'attuale ciclo (quindi dopo aver chiamato la onLateUpdate sui gameObjects abilitati) uscirà da esso per chiamare la onDeinit su tutti i gameObjects

### Delta time nanos
L'engine offre la possibilità di ricavare il tempo trascorso dallo scorso frame al frame corrente attraverso `engine.deltaTimeNanos`.

### Metodi per trovare oggetti
L'engine offre due metodi per ricercare oggetti nel gioco:
```scala
// Restituisce tutti gli oggetti trovati che implementano il behaviour B
def find[B <: Behaviour](using tt: TypeTest[Behaviour, B])(): Iterable[B]
// Restituisce un oggetto con l'identificatore dato che implementi il behaviour B
def find[B <: Identifiable](using tt: TypeTest[Behaviour, B])(id: String): Option[B]
```
Siccome l'informazione riguardante i tipi dei behaviour viene persa a runtime a causa della type erasure di Java si è dovuto utilizzare il sistema di reflection per implementare questi due metodi.
`TypeTest` permette di potersi "portare dietro" le informazioni necessarie per controllare a runtime i tipi degli oggetti.

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

## SwingIO
SwingIO è il componente grafico dell'engine, e implementa il trait IO utilizzando le funzionalità del framework Swing.

Il metodo `draw` di SwingIO permette di registrare una funzione `Graphics2D => Unit`, ovvero l'operazione da applicare al contesto grafico della finestra.
In questo modo, l'utente e i renderer possono aggiornare liberamente il proprio stato grafico semplicemente chiamando questo metodo.
Il vero aggiornamento della finestra avviene alla chiamata del metodo `show`, che esegue tutte le operazioni di rendering registrate precedentemente, ridisegnando così l'interfaccia.
Se non si chiama `show` almeno una volta, la finestra rimane nascosta.

SwingIO permette di definire la dimensione in pixel della finestra di gioco (`size`), il nome della finestra (`title`), e il colore di background (`background`). 
Inoltre, permette di lavorare con coordinate espresse non in pixels, ma in unità logiche di gioco, così da astrarre la logica dei behaviours dalla loro effettiva rappresentazione grafica.
SwingIO fornisce quindi metodi per impostare la posizione della finestra all'interno del gioco (`center`) e il numero di pixel da rappresentare per unità di gioco (`pixelsPerUnit`).
Le estensioni `pixelPosition` e `scenePosition` permettono di mappare le posizioni da coordinate in pixels a coordinate in unità di gioco, e viceversa.

Per facilitare la costruzione del SwingIO, è stato implementato un builder che offre metodi per personalizzare a piacimento l'interfaccia.

*Esempio*
```scala
val io: SwingIO = SwingIO
  .withTitle("title")                 // imposta il nome della finestra
  .withSize((800, 900))               // imposta la dimensione della finestra
  .withBackgroundColor(Color.red)     // imposta il colore di sfondo
  .withCenter((0, 0))                 // imposta la posizione logica della finestra all'interno del gioco
  .withPixelsPerUnitRatio(100)        // imposta il rapporto #pixels/unità
  .build()                            // costruisce la SwingIO
```

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

### Scalable
Un behaviour con **Scalable** come mixin ha accesso a due campi, `scaleX` e `scaleY`, che rappresentano rispettivamente quanto il behaviour dovrà essere scalato sulla X e sulla Y rispettivamente.
Il behaviour va inizializzato con entrambi i campi e se sono negativi o uguali a zero verranno settati automaticamente ad 1, mentre, se una volta che il behaviour è stato inizializzato si provano a cambiare i campi in valori negativi o uguali ad 1, essi non cambieranno.

*Esempio*
```scala
// create a scalable with scaleX = 5, scaleY = -4 (will be 1 because is negative) 
val scalable: Scalable = new Behaviour with Scalable(5, -4)
// change scaleY to 3 and try to change scaleX to -4
scalable.scaleX = -4
scalable.scaleY = 3
println(scalable.scaleX) // 5, cannot set scaleX to negative number so it didn't change
println(scalable.scaleY) // 3
```

### Collider
Un behaviour con **Collider** come mixin dovrà innanzitutto avere in mixin anche **Positionable**.
**Collider** è una semplice interfaccia che racchiude tutti i metodi `collides` che le varie forme di collider dovranno implementare estendendola. Chiamando tali metodi su un Collider si potrà verificare se esso ha avuto una collisione oppure no con il **Collider** passato come parametro.

#### RectCollider
**RectCollider** è un mixin che aggiunge ad un oggetto un collider rettangolare con il centro in `(Positionable.x, Positionable.y)` e dimensione data da `colliderWidth` e `colliderHeight` passati in input.
La sua dimensione scala in base ai valori `scaleX` e `scaleY` di **Scalable**.

*Esempio*
```scala
// Creation of a collider with dimension 5x5 at x = 0, y = 0
val collider = new Behaviour with RectCollider(5, 5) with Scalable with Positionable

// Creation of a collider with dimension 5x5 at x = 4, y = 4
val collider2 = new Behaviour with RectCollider(5, 5) with Scalable with Positionable(4, 4)

// Creation of a collider with dimension 2x2 at x = 6, y = 6
val collider3 = new Behaviour with RectCollider(2, 2) with Scalable with Positionable(6, 6)

println(collider.collides(collider2)) //true
println(collider.collides(collider3)) //false
println(collider2.collides(collider3)) //true

collider3.y = -5
collider3.x = 0
collider3.colliderHeight = 5

println(collider3.collides(collider)) //true
println(collider3.collides(collider2)) //false

```

#### CircleCollider
**CircleCollider** è un mixin che aggiunge ad un oggetto un collider tondo con il centro in `(Positionable.x, Positionable.y)` e raggio passato in input.
Il suo raggio scala in base allo `scale` di **SingleScalable**.


### SwingRenderer
Un behaviour con **SwingRenderable** come mixin potrà essere rappresentato su un IO di tipo SwingIO.
Il rendering avviene nell'evento di `onLateUpdate` del game loop, e viene fatto invocando la funzione `renderer`, che contiene l'operazione da eseguire sul SwingIO e sul suo contesto grafico.
Se l'engine non contiene un IO di tipo SwingIO, allora SwingRenderer lancia un'eccezione di tipo `ClassCastException`.

SwingRenderable è esteso dal trait **SwingGameElementRenderer**, che dovrà avere in mixin anche **Positionable** e rappresenta un oggetto di gioco qualsiasi posizionato all'interno della scena.
Questo a sua volta è esteso dai trait **SwingShapeRenderer**, che rappresenta una forma geometrica, e **SwingImageRenderer**, che rappresenta un'immagine.
Entrambi i trait hanno delle dimensioni espresse in unità di gioco, che sono modificabili e non possono avere valori negativi o nulli.
Questi renderer hanno anche un `renderOffset`, che indica di quanto il disegno debba essere traslato rispetto alla posizione attuale del behaviour.

*Esempio*
```scala
// Disegna un rettangolo in LateUpdate
val rect: SwingShapeRenderer = new Behaviour with SwingRectRenderer(width=1, height=2, color=Color.blue) with Positionable(0, 0)

rect.shapeWidth = 2           // cambia le dimensioni
rect.shapeHeight = 1
rect.renderOffset = (1, 0)    // cambia l'offset

// Disegna un cerchio in LateUpdate, con offset settato in input
val circle: SwingCircleRenderer = new Behaviour with SwingCircleRenderer(radius=2, offset=(1,0)) with Positionable(0, 0)

circle.shapeRadius = 3        // cambia il raggio di un CircleRenderer

// Disegna un'immagine in LateUpdate
val image: SwingImageRenderer = new Behaviour with SwingImageRenderer("icon.png", width=1.5, height=1.5) with Positionable(0, 0)

image.imageHeight = 2         // cambia le dimensioni
image.imageWidth = 2

```