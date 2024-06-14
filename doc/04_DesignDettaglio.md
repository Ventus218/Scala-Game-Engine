# Design di dettaglio

## [GameObject](../src/main/scala/GameObject.scala)
GameObject rappresenta un oggetto di gioco, questo ha un Behaviour che rappresenta il comporamento dell'oggetto e può avere un identificativo settato dall'utente (per potersi riferire a quell'oggetto univocamente).
Può essere attivo o disattivo, questo implica che salterà o meno certe fasi del game loop.

## [Behaviour](../src/main/scala/Behaviour.scala)
Behaviour rappresenta il comportamento di un oggetto ed è il punto principale nel quale l'utente definisce la sua logica applicativa.
Ogni behaviour possiede dei metodi che vengono chiamati dall'engine in momenti specifici del game loop:
- onInit
- onEnable
- onDisable
- onStart
- onEarlyUpdate
- onUpdate
- onLateUpdate
- onDeinit

Questi metodi ricevono come parametro un **Context** che contiene tutti i riferimenti utili per implementare il comportamento desiderato.

## [Context](../src/main/scala/Context.scala)
Context viene passato dall'engine ai metodi di **Behaviour** e contiene il riferimento all'engine per poterci interagire. E rappresenta un ottimo luogo per poter definire metodi di utilità attraverso extensions (come ad esempio scale diverse di tempo o riferimenti diretti ad IO e Storage).
Inoltre contiene il riferimento all'game object del behaviour al quale viene passato.

## [IO](../src/main/scala/IO.scala)
IO è pensato per essere implementato in modo che si possa definire un qualsiasi proprio sistema di input/output.

Le classi che implementano IO possono inoltre agganciarsi a un momento specifico del game loop ovvero la fine dell'elaborazione di un frame:
- onFrameEnd

Le specifiche implementazioni di IO forniranno ai behaviour addetti all rendering o all'input tutte le informazioni e i riferimenti di cui hanno bisogno per svolgere il loro compito.

Questo implica che l'utente utilizzando una specifica implementazione di IO assuma nei behaviour che quella sia l'implementazione sottostante.

Il caso nel quale l'utente volesse implementare più di un tipo di IO contemporaneamente sarebbe comunque realizzabile sotto al concetto di singolo IO.

## [Scene](../src/main/scala/Scene.scala)
Siccome l'engine non ha i concetti di scene o di scena attiva, ma solo di game object, allora Scene rappresenta una struttura dati che memorizza come gli oggetti andranno creati quando questa verrà caricata dall'engine.

## [SceneManager](../src/main/scala/SceneManager.scala)
SceneManager è dove sono contenute le diverse Scene definite dall'utente (ognuna con associato un nome che la identifichi).

## [Storage](../src/main/scala/Storage.scala)
Storage fornisce la possibilità all'utente di salvare coppie chiave valore in modo da permettere la persistenza di alcuni dati, cosa molto utile nel caso di passaggio da una scena all'altra.
- set
- get
- getOption
- unset

## [Engine](../src/main/scala/Engine.scala)
Engine è il motore di gioco che orchestra tutti gli altri componenti e mette a disposizione le API per farli interagire.

L'utente può:
- avviare e fermare l'engine:
    - run
    - stop
- creare o distruggere oggetti:
    - create
    - destroy
- abilitare o disabilitare oggetti:
    - enable
    - disable
- caricare gli oggetti di una scena:
    - loadScene
- cercare oggetti tra quelli presenti in gioco in base a:
    - Un tipo di behaviour che possiedono (find)
    - Un tipo di game object che sono (find)
    - Un identificativo (findById)

Inoltre engine mette a disposizione un riferimento ad IO, uno a Storage e l'informazione di quanto tempo è trascorso dall'utlimo frame (deltaTimeNanos).
