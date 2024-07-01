# Implementazione

## Engine
L'engine ha un'implementazione di default attraverso `Engine.apply()` che accetta come parametri una IO e uno Storage.

### Game Loop (run() e stop())
Una volta avviato il game loop attraverso il metodo `engine.run()`, il game loop per prima cosa chiamerà gli handler dei behaviors nel seguente ordine:

    - onInit
    - onStart (solo sui behaviours abilitati)
    Loop until stopped
        - onEarlyUpdate (solo sui behaviours abilitati)
        - onUpdate (solo sui behaviours abilitati)
        - onLateUpdate (solo sui behaviours abilitati)
    -onDeinit

I metodi `onEnabled` e `onDisabled` vengono invece invocati non appena un behaviour modifica il proprio stato da abilitato a disabilitato, e viceversa.

Chiamando il metodo `engine.stop()` l'engine capirà che si deve fermare ed una volta finito l'attuale ciclo (quindi dopo aver chiamato la onLateUpdate sui gameObjects abilitati) uscirà da esso per chiamare la onDeinit su tutti i gameObjects

### Delta time nanos
L'engine offre la possibilità di ricavare il tempo trascorso dallo scorso frame al frame corrente attraverso `engine.deltaTimeNanos`.

### Limite agli FPS (Frames Per Second)
L'engine supporta la definizione di un limite al numero massimo di fotogrammi al secondo da elaborare.

Questo viene realizzato grazie ad `FPSLimiter` che effettua una stima di quanto ogni frame dovrebbe durare, e fa aspettare l'engine in modo da rispettare questa stima.

> **Nota**:
>
> I test relativi tengono conto del tempo di esecuzione del programma, questo li rende instabili e alcuni falliscono su specifiche configurazioni durante il testing effettuato tramite le GitHub Actions. I test nel caso di fallimento vengono quindi cancellati lasciando un messaggio che inviti a ricontrollare ma senza far fallire l'intera suite di test.

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

### Caricamento scene
L'engine implementa il metodo `engine.loadScene(scene: Scene)` per poter cambiare la scena durante il gioco. Quando una nuova scena viene caricata, su tutti gli oggetti della vecchia scena viene invocato il metodo `onDeinit`, mentre su
quelli appena aggiunti viene chiamato il metodo `onInit` e, se sono abilitati, anche il metodo `onStart`.

L'inserimento effettivo dei game object presenti nella scena da caricare avviene alla fine del frame corrente, tra il _LateUpdate_ del frame precedente e l'_EarlyUpdate_ del frame successivo.

### Creazione/Distruzione degli oggetti
L'engine offre la possibilità di aggiungere e togliere oggetti dalla scena dinamicamente, tramite i metodi `engine.create(object: Behaviour)` e `engine.destroy(object: Behaviour)`. Qualsiasi behavior può utilizzare queste due funzioni per
modificare gli oggetti attivi durante il gioco, senza alterare le fasi del game loop. Questi due metodi non vengono però applicati immediatamente sull'engine, per cui se si crea/distrugge un oggetto in una fase di update, il cambiamento
potrà essere visibile solamente dal frame successivo.

La creazione di un game object comporta la chiamata del metodo `onInit` su quest'ultimo all'inizio del frame successivo, prima dell'_EarlyUpdate_, e anche del metodo `onStart` se l'oggetto è abilitato.
In questo modo, anche gli oggetti creati dinamicamente durante il gioco rispettano le fasi del ciclo di vita dei behaviour, così da non avere side-effect indesiderati.
Se si vuole creare un oggetto che esiste già nella scena, viene lanciata una `IllegalArgumentException`.

La distruzione di un game object comporta la chiamata del metodo `onDeinit` su quest'ultimo alla fine del frame corrente. Se si vuole distruggere un oggetto che non è presente nella scena, viene lanciata una `IllegalArgumentException`.

### Abilitazione e disabilitazione degli oggetti
E' possibile abilitare e disabilitare gli oggetti di gioco in maniera dinamica.

Abilitare un oggetto implica che nel frame successivo venga chiamata la *onEnabled* sull'oggetto.:
```scala
val obj = engine.find[Identifiable]("id").get
engine.enable(obj)
```

Si abilita l'oggetto solo nel frame successivo per dare consistenza, in quanto, se così non fosse, l'ordine di esecuzione degli oggetti potrerebbe alcuni oggetti ad essere abilitati nel frame corrente e altri nel frame successivo. 

Disabilitare un oggetto implica che venga chiamata la *onDisabled* sull'oggetto e che questo venga disabilitato solo alla fine del frame.
```scala
val obj = engine.find[Identifiable]("id").get
engine.disable(obj)
```

Il motivo per cui *onDisabled* viene chiamata a fine frame e non immediatamente è che altrimenti sarebbe possibile che l'oggetto riceva delle chiamate a *onEarlyUpdate/onUpdate/onLateUpdate* dopo la chiamata a *onDisabled*.

> **Nota:**
>
> *onEnabled* e *onDisabled* vengono invocate solo in caso di cambiamento di stato, e non se l'oggetto viene instanziato già abilitato o disabilitato.

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

Il metodo `draw` di SwingIO permette di registrare una funzione `Graphics2D => Unit`, ovvero l'operazione da applicare al contesto grafico della finestra, e la sua priorità, ovvero l'ordine con il quale queste operazioni vengono applicate.
In questo modo, l'utente e i renderer possono aggiornare liberamente il proprio stato grafico semplicemente chiamando questo metodo.
Il vero aggiornamento della finestra avviene alla chiamata del metodo `show`, che esegue tutte le operazioni di rendering registrate precedentemente, ridisegnando così l'interfaccia.
Se non si chiama `show` almeno una volta, la finestra rimane nascosta.

Per disegnare su schermo i vari renderer, viene utilizzata la classe di utility `DrawableCanvas`, che è un JPanel di Swing con il metodo `paintComponent` modificato per poter applicare sul proprio oggetto grafico anche le operazioni
registrate con la `draw`. Per ottimizzare le prestazioni ed evitare il lampeggiamento degli oggetti sulla scena, si usa una tecnica di buffering: SwingIO utilizza due buffer per disegnare, chiamati `activeCanvas` e `bufferCanvas`; `activeCanvas` è il canvas
visibile all'utente, mentre `bufferCanvas` quello nascosto. Tutte le operazioni di rendering vengono eseguite sul `bufferCanvas` mentre non è visibile, e quando viene invocato il metodo `show`, i due canvas vengono scambiati, rendendo visibili i cambiamenti sulla view.

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
- Il behaviour [SwingInputHandler](#swinginputhandler) permette all'utente di definire delle associazioni `tasto premuto -> azione da eseguire`, queste azioni verrano eseguite durante la fase di EarlyUpdate. Questo permette un approccio event driven piuttosto che a polling.
  
  Le azioni devono essere eseguite ad ogni frame se il bottone è stato premuto e rilasciato, premuto o tenuto premuto.

### Implementazione
SwingIO (in particolare SwingInputEventsAccumulator) accoda tutti gli eventi (pressioni e rilasci di tasti) inviati da Swing. Una volta raggiunta la fine del frame la coda degli eventi viene copiata (in modo da renderla persistente fino al prossimo frame) e svuotata cosicchè i nuovi eventi possano continuare ad essere accodati. 

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
Il metodo che implementa tale logica è:
```scala
trait SwingIO extends IO:
    // ...
    def inputButtonWasPressed(inputButton: InputButton): Boolean
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

### PositionFollower
**PositionFollower** è un mixin che accetta come parametro un `followed` di tipo **Positionable** e un `offset` del tipo `(Double, Double)`, ed esso stesso richiede in mixin un **Positionable**.
Il **PositionFollower** si occupa di tenere aggiornata la posizione del proprio **Positionable** in base alla posizione del `followed`, aggiungendoci l'`offset`.
La posizione viene inizializzata nella `onInit` e aggiornata nella `onLateUpdate`.

### Scalable
**Scalable** è un mixin generico su un tipo `T` che ne rappresenta la dimensione su cui scalare i valori. Per esempio, se si vuole scalare un singolo valore `Double`, allora il tipo `T` sarà proprio `Double`, se invece si vogliono scalare due valori `Double`, il tipo `T` sarà `(Double, Double)`.
Oltre al tipo generico e ad un valore di inizializzazione dello scaling, **Scalable** utilizza un contesto di tipo **IsValid** generico anch'esso sul tipo `T`, che si occuperà di indicare se lo scaling è valido oppure no.
Sono presenti due implementazioni di default del contesto **IsValid**, una per `Double` e una per `(Double, Double)`, in modo che controlli che lo scaling sia positivo e maggiore di zero (nel caso di due dimensioni, entrambe devono essere positive e maggiori di zero).
Se il parametro in input al costruttore di **Scalable** non è valido secondo il contesto, viene tirata un'eccezzione del tipo `IllegalArgumentException`.

*Esempio*
```scala
// create a scalable that scales two dimensions, with (1, 1) as value of the scaling
val scalable: Scalable[(Double,Double)] = new Behaviour with Scalable(1d, 1d) // equivalent to Scalable((1d, 1d)) in Scala
scalable.scale = (4, 3)
println(scalable.scale) // (4, 3)
scalable.scale = (10, -2)
// it is not possible to change either one of the values if one of them is not valid (according to the given context)
println(scalable.scale) // (4, 3)

val singleScalable: Scalable[Double] = new Behaviour with Scalable(1.0)
singleScalable.scale = 5
println(singleScalable.scale) // 5
```

### Collider
Un behaviour con **Collider** come mixin dovrà innanzitutto avere in mixin anche **Positionable**.
**Collider** è una semplice interfaccia che racchiude tutti i metodi `collides` che le varie forme di collider dovranno implementare estendendola. Chiamando tali metodi su un Collider si potrà verificare se esso ha avuto una collisione oppure no con il **Collider** passato come parametro.

#### RectCollider
**RectCollider** è un mixin che aggiunge ad un oggetto un collider rettangolare con il centro in `(Positionable.x, Positionable.y)` e dimensione data da `colliderWidth` e `colliderHeight` passati in input.
La sua dimensione scala in base allo `scale` di **Scalable**.

*Esempio*
```scala
// Creation of a collider with dimension 5x5 at x = 0, y = 0
val collider = new Behaviour with RectCollider(5, 5) with Scalable(1.0, 1.0) with Positionable

// Creation of a collider with dimension 5x5 at x = 4, y = 4
val collider2 = new Behaviour with RectCollider(5, 5) with Scalable(1.0, 1.0) with Positionable(4, 4)

// Creation of a collider with dimension 2x2 at x = 6, y = 6
val collider3 = new Behaviour with RectCollider(2, 2) with Scalable(1.0, 1.0) with Positionable(6, 6)

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
Il suo raggio scala in base allo `scale` di **Scalable**.


### SwingRenderer
Un behaviour con **SwingRenderable** come mixin potrà essere rappresentato su un IO di tipo SwingIO.
Il rendering avviene nell'evento di `onLateUpdate` del game loop, e viene fatto invocando la funzione `renderer`, che contiene l'operazione da eseguire sul SwingIO e sul suo contesto grafico, insieme con la `rendereringPriority` che indica la priorità da passare allo SwingIO.
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

### SwingInputHandler
Permette allo sviluppatore di definire associazioni del tipo `input -> azione`

```scala
class GameObject
      extends Behaviour
      // ...
      with SwingInputHandler:

    var inputHandlers: Map[InputButton, Handler] = Map(
        D -> onMoveRight,
        A -> onMoveLeft,
        W -> onMoveUp,
        S -> onMoveDown,
        E -> (onMoveRight and onMoveUp),
        MouseButton1 -> onTeleport, // same as (onTeleport.onlyWhenPressed and onTeleport.onlyWhenHeld)
        MouseButton3 -> onTeleport.onlyWhenPressed,
        MouseButton2 -> onTeleport.onlyWhenHeld,
        Space -> onTeleport.onlyWhenReleased
    )

    private def onTeleport(input: InputButton)(engine: Engine): Unit = // teleport logic
    private def onMoveRight(input: InputButton)(engine: Engine): Unit = // move logic
    private def onMoveLeft(input: InputButton)(engine: Engine): Unit = // move logic
    private def onMoveUp(input: InputButton)(engine: Engine): Unit = // move logic
    private def onMoveDown(input: InputButton)(engine: Engine): Unit = // move logic
```

Il motivo per cui si obbliga la classe che implementa a definire inputHandlers piuttosto che accettare inputHandlers come parametro del mixin è che questo permette allo sviluppatore di avere i riferimenti ai metodi interni alla classe, altrimenti non sarebbe possibile avere una sintassi così espressiva.

Come si può notare dall'esempio è anche possibile definire handler complessi:
- è possibile definire un handler che esegua più funzioni tramite la sintassi `handler1 and handler2`
- su ogni handler è possibile applicare un modificatore, i modificatori servono a specificare in quali situazioni dell'input l'handler va eseguito, ovvero:
    - `onlyWhenPressed` l'handler viene eseguito solo nel primo frame nel quale un input viene ricevuto, anche se l'input persiste per più frame
    - `onlyWhenReleased` l'handler viene eseguito solo nel frame in cui un input passa da essere ricevuto a non esserlo più
    - `onlyWhenHeld` l'handler viene eseguito in tutti i frame (dopo il primo) per cui l'input viene ricevuto.

  Questi modificatori, soprattutto se composti, consentono la massima espressività.
  
> **Nota:**
> 
> Quando si fornisce una funzione questa avrà un comportamento di default che è quello più comunemente utilizzato, ovvero esecuzione in ogni frame in cui l'input è ricevuto (sia il primo che tutti quelli successivi)
> ```scala
> MouseButton1 -> onTeleport
> // is exactly the same as 
> MouseButton1 -> (onTeleport.onlyWhenPressed and onTeleport.onlyWhenHeld)
> ```

Nel caso si preferisse comunque un approccio non event-driven è possibile utilizzare direttamente SwingIO senza SwingInputHandler:

```scala
class GameObject extends Behaviour
    override def onUpdate: Engine => Unit = (engine) =>
        val io: SwingIO = // ...

        if io.inputButtonWasPressed(SPACE) then
            // jump logic ...

        super.onUpdate(engine)
```
