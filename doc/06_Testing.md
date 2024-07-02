# Testing
Testare un loop non è sicuramente banale, per questo si sono preparati degli strumenti per semplificare il testing. Si è cercato inoltre di consentire una sintassi leggera e domain specific.

## Come testare il game loop
Per testare il game loop si è scelto di utilizzare degli oggetti di gioco (Behaviour).

L'idea è che l'oggetto di gioco offre la possibilità di eseguire test in ogni punto del game loop e proprio per questo è possibile iniettare nell'engine degli oggetti e utilizzarli come tester.

## TestUtils
Il modulo `TestUtils` racchiude tutte le utility necessarie per semplificare il testing del gameloop.

### `testOnGameloopEvents`
Questa è la funzione (fornita come extension su `Engine`) per testare il gameloop, accetta una scena e un numero che rappresenta per quanti frame l'engine dovrà essere eseguito. Inoltre fornisce un [builder](#testerobjectbuilder) per definire la logica di testing sugli specifici eventi del gameloop.

### `TesterObjectBuilder`
Questo builder permette di configurare l'oggetto tester che verrà iniettato nella scena, permette quindi di definire una [`TestingFunction`](#testingfunction) per ogni fase del gameloop che verrà eseguita quando l'oggetto tester arriverà in tale fase.
Inoltre permette anche di definire per quanti frame eseguire l'engine prima di fermarlo automaticamente.

### `TestingFunction`
E' un type alias per `TestingContext => Unit` ovvero una funzione alla quale viene passato un [contesto di testing](#testingcontext) e che esegue una computazione (solitamente il test vero e proprio).

Per permettere l'utilizzo di una sintassi minimale in fase di scrittura dei test si sono anche definite delle conversioni implicite da `Unit` e `Assertion` a `TestingFunction`.
In questo modo è possibile passare un semplice blocco di codice o delle asserzioni di scalatest piuttosto che una funzione con `TestingContext` come parametro (che spesso non è necessario).

> **Nota:**
>
> Si sarebbe potuto anche definire una sola conversione implicita da `Any` a `TestingFunction` però si è deciso di non farlo in quanto potrebbe essere poco intuitivo. Per il momento sono quindi abbastanza le conversioni su `Unit` e `Assertion`, ma nel caso in futuro fosse necessario è possibile definire al loro posto una da `Any`.

### `TestingContext`
Alcuni metodi accettano come funzioni di test del tipo `(TestingContext) => Unit`. `TestingContext` contiene riferimenti utili, in particolare un riferimento all'oggetto tester permettendo di riconoscerlo ed escluderlo da alcune logiche di test.

### Altri metodi per testare l'`Engine`
- `loadSceneTestingOnGameloopEvents` come [`testOnGameloopEvents`](#testongameloopevents) però pensata per essere utilizzata mentre l'engine è già in esecuzione per cambiare scena iniettando nuovamente un oggetto tester.
- `testWithTesterObject` è il metodo di test più generico disponibile.

    Accetta una scena su cui lanciare l'engine, e un oggetto tester (che servirà ad eseguire le funzioni di test e verrà iniettato nell'engine).
- `loadSceneWithTesterObject` come testWithTesterObject però pensata per essere utilizzata mentre l'engine è già in esecuzione per cambiare scena iniettando il nuovo oggetto tester.

Tutti questi metodi sono forniti come extension su `Engine`

## Utilizzo di TestUtils

```scala
// Verrà testato che nella fase di update non sono
// presenti in gioco oggetti mixati con Identifiable
"find(id)" should "retrieve no object if none is found with the given identifier" in:
    engine.testOnUpdate(scene): // avvio dell'engine sulla scena "scene"
        engine.find[Identifiable]("3") shouldBe None // funzione di test
```

```scala
// Si vuole testare che cercando Behaviour si trovino
// tutti i game object della scena.
"find" should "retrieve all objects if Behaviour is given as type parameters" in:
    // Si noti che in questo caso serve conoscere quale fosse
    // l'oggetto tester per poterlo escludere.
    engine.testOnUpdateWithContext(scene): (testingContext) =>
        engine.find[Behaviour]() should contain theSameElementsAs gameObjects + testingContext.testerObject
```

```scala
// Si vuole testare che al primo frame il delta time sia 0
"Engine.deltaTimeNanos" should "be 0 for all the iteration of the game loop" in:
    engine.testOnEarlyUpdate(testScene):
        engine.deltaTimeNanos shouldBe 0

    engine.testOnUpdate(testScene):
        engine.deltaTimeNanos shouldBe 0

    engine.testOnLateUpdate(testScene):
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
    engine.testOnDeinit(oneFrameScene, nFramesToRun = 5):
        /** This tests has to deal with undeterministic behaviour:
        *
        * Given the fact that the order of objects is not defined. The tester
        * object may run its test while other objects "onDeinit" may not have
        * been called yet. This is why the test succedes in both cases.
        */
        engine
        .find[LifecycleTester]()
        .filter(_.enabled)
        .foreach(
            _.happenedEvents should (
            contain theSameElementsInOrderAs sequenceOfActions :+ Deinit
                or contain theSameElementsInOrderAs sequenceOfActions
            )
        )
```

```scala
"deltaTimeNanos" should "be higher than time elapsed inside updates (but not too much higher)" in:
    // ...

    val deinitTesterFunction: (TestingContext) => Unit = (testingContext) =>
        //...

    // Si noti come è possibile definire dei tester custom
    // grazie ai mixin e iniettarli nell'engine
    engine.testWithTesterObject():
        new Behaviour
            with DeinitTester(deinitTesterFunction)
            with NFrameStopper(1)
            with SlowEarlyUpdater(slowDownDurationMillis)
            with SlowUpdater(slowDownDurationMillis)
            with SlowLateUpdater(slowDownDurationMillis)
```