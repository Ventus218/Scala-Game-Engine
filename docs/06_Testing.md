# Testing
Testare un loop non è sicuramente banale, per questo si sono preparati degli strumenti per semplificare il testing. Si è cercato inoltre di consentire una sintassi leggera e *domain specific*.

## Come testare il game loop
Per testare il game loop si è scelto di utilizzare degli oggetti di gioco (Behaviour).

L'idea è che l'oggetto di gioco offre la possibilità di eseguire test in ogni punto del game loop e proprio per questo è possibile iniettare nell'engine degli oggetti e utilizzarli come tester.

## TestUtils
Il modulo `TestUtils` racchiude tutte le utility necessarie per semplificare il testing del gameloop.

### `TestBuilder`
Questo builder consente di configurare gli aspetti fondamentali di un test ovvero:
- su quale engine eseguirlo
- quale scena caricare
- per quanti frame eseguire il test prima di fermare automaticamente l'engine

Per ottenere un test builder basta semplicemente chiamare la funzione `test` con l'engine come parametro:
```scala
test(engine)
```
Ora è possibile configurarlo con una sintassi domain-specific grazie a dei metodi specifici come:
- `runningFor` che richiede il numero di frame
- `on` che richiede la scena da caricare

Per eseguire il test è necessario invocare `that` oppure `soThat` in modo da ottenere il [`TesterObjectBuilder`](#testerobjectbuilder) per definire la logica di testing sugli specifici eventi del gameloop.

[Qui](#utilizzo-di-testutils) è possibile vedere come risulta la sintassi.

### `TesterObjectBuilder`
Questo builder permette di configurare l'oggetto tester che verrà iniettato nella scena, consente quindi di definire una [`TestingFunction`](#testingfunction) per ogni fase del gameloop che verrà eseguita quando l'oggetto tester arriverà in tale fase.
Inoltre permette anche di definire per quanti frame eseguire l'engine prima di fermarlo automaticamente.

### `TestingFunction`
E' un type alias per `TestingContext => Unit` ovvero una funzione alla quale viene passato un [contesto di testing](#testingcontext) e che esegue una computazione (solitamente il test vero e proprio).

Per permettere l'utilizzo di una sintassi minimale in fase di scrittura dei test si è definita una conversion implicita da `Any` a `TestingFunction`.
In questo modo è possibile passare un semplice blocco di codice o delle asserzioni di scalatest piuttosto che una funzione con `TestingContext` come parametro (che spesso non è necessario).

### `TestingContext`
Le `TestingFunction` ricevono un `TestingContext` che contiene riferimenti utili, in particolare un riferimento all'oggetto tester permettendo di riconoscerlo ed escluderlo da alcune logiche di test.

### Altri metodi per testare l'`Engine`
- `loadSceneTestingOnGameloopEvents` è pensata per essere utilizzata mentre l'engine è già in esecuzione per cambiare scena iniettando nuovamente un oggetto tester.
- `testWithTesterObject` è il metodo di test più generico disponibile.

    Accetta una scena su cui lanciare l'engine, e un oggetto tester (che servirà ad eseguire le funzioni di test e verrà iniettato nell'engine).
- `loadSceneWithTesterObject` come testWithTesterObject però pensata per essere utilizzata mentre l'engine è già in esecuzione per cambiare scena iniettando il nuovo oggetto tester.

Tutti questi metodi sono forniti come extension su `Engine`

## Utilizzo di TestUtils
Tutti i test possono utilizzare una sintassi del seguente tipo:
```scala
// Si legge "test engine on testScene runningFor 2 frames so that onUpdate x shouldBe 3
test(engine) on testScene runningFor 2 frames so that: 
    _.onUpdate:
        x shouldBe 3

// E' anche possibile non specificare il numero di frame, nel qual caso di default verrà eseguito un solo frame
test(engine) on testScene soThat: 
    _.onUpdate:
        x shouldBe 3
```

```scala
// Verrà testato che nella fase di update non sono
// presenti in gioco oggetti mixati con Identifiable
"find(id)" should "retrieve no object if none is found with the given identifier" in:
    test(engine) on scene soThat: // avvio dell'engine sulla scena "scene"
        _.onUpdate: // configurazione del builder per l'evento onUpdate
            engine.find[Identifiable]("3") shouldBe None // funzione di test
```

```scala
// Si vuole testare che cercando Behaviour si trovino
// tutti i game object della scena.
"find" should "retrieve all objects if Behaviour is given as type parameters" in:
    // Si noti che in questo caso serve conoscere quale fosse
    // l'oggetto tester per poterlo escludere.
    test(engine) on scene soThat:
        _.onUpdate: (testingContext) => // Si noti come la sintassi è la stessa ma basta aggiungere il parametro
            engine.find[Behaviour]() should contain theSameElementsAs gameObjects + testingContext.testerObject
```

```scala
// Si vuole testare che al primo frame il delta time sia 0
"Engine.deltaTimeNanos" should "be 0 for all the iteration of the game loop" in:
    test(engine) on testScene soThat:
        // si noti come è semplice configurare il builder
        _.onEarlyUpdate:
            engine.deltaTimeNanos shouldBe 0
        .onUpdate:
            engine.deltaTimeNanos shouldBe 0
        .onLateUpdate:
            engine.deltaTimeNanos shouldBe 0

```

```scala
"Engine" should "stop when engine.stop() is called" in:
    val oneFrameScene = testScene.joined: () =>
        Seq(new Behaviour with NFrameStopper(1))

    var sequenceOfActions =
        getSequenceOfActions() ++ getUpdatesSequenceOfActions()

    // L'idea è che i test facciano girare l'engine per 5 volte, ma dato che
    // un NFrameStopper(1) è stato aggiunto, si fermerà dopo un solo frame
    test(engine) on oneFrameScene runningFor 5 frames so that:
        _.onDeinit:
            /** This tests has to deal with undeterministic behaviour:
            *
            * Given the fact that the order of objects is not defined. The tester
            * object may run its test while other objects "onDeinit" may not have
            * been called yet. This is why the test succedes in both cases.
            */
            engine
            .find[GameloopTester]()
            .filter(_.enabled)
            .foreach(
                _.happenedEvents should (
                    contain theSameElementsInOrderAs sequenceOfActions :+ Deinit
                    or contain theSameElementsInOrderAs sequenceOfActions
                )
            )
```

```scala
"Engine.loadScene" should "change the scene after finishing the current frame" in:
    test(engine) on scene1 soThat:
        _.onEarlyUpdate:
            // Si noti come cambiando scena è necessario ridefinire il testing da effettuare
            engine.loadSceneTestingOnGameloopEvents(scene2):
            _.onUpdate:
                engine.find[Identifiable]() should contain theSameElementsAs scene2()
        .onLateUpdate:
            engine.find[Identifiable]() should contain theSameElementsAs scene1()
```

## Testing di Swing

Essendo Swing un framework per la realizzazione di interfacce grafiche il testing dei componenti non è semplice. Per questo si è fatto il possibile per testare ciò che poteva essere direttamente testato e invece per quanto riguarda i componenti grafici o visivi si sono realizzati tanti piccoli `@main` da eseguire manualmente per verificare che tutto funzioni.
