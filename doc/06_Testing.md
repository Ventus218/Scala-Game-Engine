# Testing

Testare un loop non è sicuramente banale, per questo si sono preparati degli strumenti per semplificare il testing.

## Come testare il game loop

Per testare il game loop si è scelto di utilizzare degli oggetti di gioco (Behaviour).

L'idea è che l'oggetto di gioco offre la possibilità di eseguire test in ogni punto del game loop e proprio per questo è possibile iniettare nell'engine degli oggetti e utilizzarli come tester.

## TestUtils
Per semplificare il testing si sono creati dei trait e metodi di utilità.

Qui sotto si mostrano le utility per testare l'evento di **Update**, quelle per gli altri eventi sono analoghe.

```scala
object TestUtils:
    object Testers:
        // ...
        trait UpdateTester(testFunction: (TestingContext) => Unit) extends Behaviour:
            override def onUpdate: Engine => Unit = (engine) =>
                testFunction(TestingContext(engine, this))
                super.onUpdate(engine)
        // ...

    extension (engine: Engine)
    // ...
    def testWithTesterObject(scene: Scene = () => Seq.empty)(testerObject: Behaviour): Unit =
        engine.run(scene.joined(() => Seq(testerObject)))

    def testOnEarlyUpdateWithContext(scene: Scene = () => Seq.empty, nFramesToRun: Int = 1)(testFunction: (TestingContext) => Unit): Unit =
        val testerObject = new Behaviour
            with EarlyUpdateTester(testFunction)
            with NFrameStopper(nFramesToRun)
        engine.testWithTesterObject(scene)(testerObject)
    // ...
```
### UpdateTester
E' un trait che può essere mixato ad un oggetto che accetta ed esegue la funzione di test nella fase di **Update**

### Metodi extension su Engine
- `testWithTesterObject` è il metodo di test più generico disponibile.

    Accetta una scena su cui lanciare l'engine, e un oggetto tester (che servirà ad eseguire le funzioni di test e verrà iniettato nell'engine).
- `testOnEarlyUpdateWithContext` metodo specifico per testare l'update, sfrutta un `UpdateTester` e permette di definire per quanti frame eseguire l'engine.

A molti dei metodi è possibile passare un parametro *nFramesToRun* che indica per quanti frame eseguire l'engine prima di fermarlo.
In caso di tester personalizzati è possibile utilizzare in mixin il trait `NFrameStopper`
    
### TestingContext
Alcuni metodi accettano come funzioni di test del tipo `(TestingContext) => Unit`. `TestingContext` contiene riferimenti utili, in particolare un riferimento all'oggetto tester permettendo di riconoscerlo ed escluderlo da alcune logiche di test.

## Come utilizzare TestUtils
Si è cercato di rendere l'esperienza di testing il più semplice ed intuitiva possibile, di seguito alcuni esempi su come è possibile utilizzare gli strumenti forniti.

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