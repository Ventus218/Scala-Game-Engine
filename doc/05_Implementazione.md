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

## SwingIO (Input)
### Architettura
Si è implementata la seguente architettura:
- SwingIO registra gli eventi di input generati dall'utente.
- Per gestire la concorrenza della ricezione degli eventi si è deciso di accumularli durante l'esecuzione di un frame e gestirli solo nel frame successivo.
- Il behaviour SwingInputHandler permette all'utente di definire delle associazioni `tasto premuto -> azione da eseguire`, queste azioni verrano eseguite durante la fase di EarlyUpdate. Questo permette un approccio event driven piuttosto che a polling.
  
  Le azioni devono essere eseguite ad ogni frame se il bottone è stato premuto e rilasciato, premuto o tenuto premuto.

### Implementazione
SwingIO accoda tutti gli eventi (pressioni e rilasci di tasti) inviati da Swing. Una volta raggiunta la fine del frame la coda degli eventi viene copiata (in modo da renderla persistente fino al prossimo frame) e svuotata cosicchè i nuovi eventi possano continuare ad essere accodati. 

Per semplicità si immagini che gli eventi siano raggruppati per tasto.

A questo punto è possibile analizzare gli eventi per ogni tasto e decidere se l'azione associata deve essere eseguita.

Si prenda in considerazione un tasto T, gli eventi P = premuto e R = rilasciato, i casi di eventi accumulati possono essere i seguenti:
|Ultimo evento registrato (prima della coda)|Coda eventi (a destra il più recente)|Interpretazione|Esecuzione dell'azione associata|
|:----------------------:|-------------------------------------|-----------|:---------:|
|| [ ]             |T non è mai stato premuto o rilasciato| |
|| [ P ]           |T è stato premuto per la prima volta| X |
|| [ R, P, ... ]        |T è stato premuto per la prima volta (il primo R si può ignorare in maniera sicura)| X |
|| [ P, R, ... ]        |T è stato premuto e rilasciato nell'arco del frame per la prima volta| X |
|P| [ ]             |T era stato premuto ed è ancora premuto| X |
|P| [ R ]           |T era stato premuto e viene rilasciato| |
|P| [ R, P, ... ]        |T era stato premuto e nell'arco del frame è stato rilasciato e premuto| X |
|R| [ ]             |T era stato rilasciato| |
|R| [ P ]           |T era stato rilasciato e viene premuto| X |
|R| [ P, R, ... ]        |T era stato rilasciato e nell'arco del frame è stato premuto e rilasciato| X |

> **Nota:**
>
> Si sono escluse configurazioni che possono essere ignorate in maniera sicura, ad esempio quelle in cui T era stato premuto prima dell'avvio dell'applicazione e rilasciato dopo.

Per semplicità si considera al massimo una esecuzione per frame dell'azione associata, questo significa che in una coda più ripetizioni di P vengono combinate in una sola.
In futuro, in caso di necessità rimane possibile modificare questo comportamento.

Il tutto può essere condensato in una espressione più semplice, l'azione deve essere eseguita se:
**E' presente almeno un P nella coda OR (La coda è vuota AND l'ultimo evento è P)**

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
Un behaviour con **Collider** come mixin dovrà innanzitutto avere in mixin anche **Dimensionable** e **Positionable**. Al **Collider** potranno essere passati due valori, `w` e `h`, rispettivamente la sua larghezza e la sua altezza.
Un valore inferiore o uguale a 0 per i due campi (valore di default = 0) comporta che larghezza e altezza del **Collider** siano gli stessi del **Dimensionable**.

Questi due valori potranno poi essere recuperati dall'esterno attraverso i campi `colliderWidth` e `colliderHeight` che non potranno essere cambiati in valori negativi o uguali a 0.

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

collider3.y = -5
collider3.x = 0
collider3.colliderHeight = 5

println(collider3.collides(collider)) //true
println(collider3.collides(collider2)) //false

```

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
