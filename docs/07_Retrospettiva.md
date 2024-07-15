# Retrospettiva

## Valutazione del processo di sviluppo
La realizzazione dell'intero progetto si è sviluppata nell'arco di 5 sprint. Delle quali la prima si è lavorato interamente in gruppo e l'ultima solo singolarmente.

Nel complesso ci siamo trovati bene ad adottare il modello SCRUM-like suggerito dal professore, ci ha aiutato a suddviderci equamente il lavoro e a favorire la collaborazione.

### Git Flow
Anche Git Flow è stato un'ottima scelta, non ci siamo mai trovati in particolari difficoltà dovute a questo branching model.

### Continuous integration
L'utilizzo di stumenti offerti da GitHub come code review, pull request, e actions hanno facilitato parecchio la coordinazione e velocizzato lo svolgimento di azioni ripetitive.

## Valutazione del risultato
Siamo complessivamente molto soddisfatti per quanto riguarda lo sviluppo dell'engine, tutti avevamo già avuto qualche esperienza nell'utilizzo di altri game engine (Unity), e riteniamo di aver catturato i concetti più importanti riguardanti lo sviluppo di videogiochi.

### Approccio basato su Mixin
E' stato sicuramente interessante seguire questo approccio, che ha dato vita a diverse soluzioni molto espressive. Riteniamo che renda la definizione di oggetti di gioco in maniera piuttosto intuitiva (in quanto è possibile vedere l'oggetto come un tutt'uno e non come un insieme di componenti scollegati tra di loro).

Siamo comunque venuti incontro anche a qualche problematica, in particolare riguardante conflitti che potevano generarsi tra i nomi dei campi utilizzati nei mixin, riteniamo che comunque non sia un problema grave in quanto con le giuste precauzioni è possibile ridurne l'occorrenza.

Una cosa che non ci ha soddisfatto a pieno è il fatto che se un mixin richiede dei parametri "nel costruttore" questi possano essere passati solo dalla classe che effettivamente lo include e non da altri mixin nel mezzo

```scala
// Questo non compila
trait A(var text: String, var color: Color) extends Behaviour
trait DefaultA(color: Color) extends Behaviour with A("Default", color)
class GameObject extends Behaviour with A with DefaultA

// Questo è invece il modo in cui si deve procedere
trait A(var text: String, var color: Color) extends Behaviour
trait DefaultA() extends Behaviour with A
class GameObject extends Behaviour with A("Default", Color.blue) with DefaultA

// Oppure è possibile utilizzare una classe,
// questo però impedirà di mixarla e permetterà solo di ereditare
trait A(var text: String, var color: Color) extends Behaviour
class DefaultA(var color: Color) extends Behaviour with A("Default", color)
class GameObject extends DefaultA(Color.blue)
```

### DSL
Inizialmente avevamo deciso di realizzare un DSL per permettere allo sviluppatore di definire le scene in maniera semplice e intuitiva. Ad un certo punto però ci siamo resi conto che l'approccio a mixin ci aveva portato direttamente in questa direzione e che un DSL in questo ambito non avrebbe portato a nessun tipo di vantaggio.

```scala
class GameObject(
        initX: Double,
        initY: Double,
        circleRadius: Double,
        val movementVelocity: Double = 40
    ) extends Behaviour
        with Positionable(initX, initY)
        with SingleScalable(1.0)
        with CircleRenderer(circleRadius, Color.blue)
        with CircleCollider(circleRadius)
        with InputHandler
        with Velocity
        with Acceleration((0, -100)):
    // implementazione specifica di GameObject

class Obstacle(initX: Double, initY: Double, squareSide: Double)
        extends Behaviour
        with Positionable(initX, initY)
        with Scalable(1.0, 1.0)
        with ImageRenderer("epic-crocodile.png", squareSide, squareSide)
        with RectCollider(squareSide, squareSide):
    // implementazione specifica di Obstacle

// Il fatto di aver realizzato degli oggetti (classi qui sopra) in maniera così
// componibile ci permette poi di avere una inizializzazione molto intuitiva
// e semplice, come è possibile vedere qui sotto
object GameScene extends Scene:
    override def apply(): Iterable[Behaviour] =
        Seq(
            GameObject(
                initX = 0,
                initY = -20,
                circleRadius = 5
            ),
            Obstacle(
                initX = 0,
                initY = 0,
                squareSide = 5
            ),
            Obstacle(
                initX = 10,
                initY = 20,
                squareSide = 25
            ),
            Obstacle(
                initX = -20, 
                initY = -30,
                squareSide = 10
            ),
            Obstacle(
                initX = 40, 
                initY = 20, 
                squareSide = 2
            )
        )
```

Per questo motivo abbiamo deciso di realizzare invece un DSL per le API di testing dell'engine, per un esempio vedere [qui](./06_Testing.md#L55).

## Giochi realizzati
Abbiamo realizzato ben 3 giochi con caratteristiche e gameplay differenti, e riteniamo che l'engine si sia dimostrato sufficientemente general purpose.

Avendo ognuno di noi sviluppato il proprio gioco in maniera autonoma sono emersi diversi approcci all'integrazione di un approccio funzionale in un motore di gioco orientato agli oggetti.

## Ulteriori osservazioni
Avendo fatto dei refactoring importanti durante lo sviluppo l'aver utilizzato il TDD ci ha permesso di avere molta sicurezza nel effettuare cambiamenti e inoltre non ci siamo quasi mai trovati nella situazione di dover scovare bug particolarmente insidiosi.
