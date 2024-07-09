# Briscola
Si vuole realizzare il famoso gioco di carte da spiaggia (per due giocatori).

Si è scelto questo gioco in quanto essendo a turni differisce abbastanza dagli altri giochi e questo permette di provare che l'engine sia sufficientemente general purpose.

Inoltre presenta una struttura e una logica di funzionamento moderatamente complessa che dovrebbe consentire di dimostrare le capacità di programmazione funzionale acquisite durante l'insegnamento.

## Architettura
Si vuole realizzare un cuore funzionale con guscio ad oggetti (dove interfacciarsi con il game engine).
Questo approccio dovrebbe garantire il meglio di entrambi i paradigmi.

Quindi l'intero modello del gioco della briscola è stato realizzato in maniera puramente funzionale. Mentre la parte grafica e di interazione con il giocatore sono state implementate utilizzando l'engine seguendo il paradigma di programmazione orientato agli oggetti.

## Principi di design
Per quanto riguarda il modello si è deciso di sperimentare alcuni principi di design visti su [fsharpforfunandprofit.com](https://fsharpforfunandprofit.com/video/), come:

- **Sfruttare il type system per definire la logica applicativa**
    
     (per un esempio vedere [`ShuffledDeck`](#shuffleddeck))
- **Definire il dominio direttamente nel codice**
    
    Sostanzialmente si vuole offrire l'interfaccia minima che permetta di lavorare con il modello garantendo così che sia anche impossibile utilizzarlo impropriamente.


## Design di dettaglio
### [`Cards`](/Trump/src/main/scala/model/Cards.scala)
Le carte di gioco hanno un seme (`Suit`) e un grado (`Rank`).

Il grado determina anche la forza di una carta e il suo valore in punti una volta acquisita dal giocatore.

### [`Decks`](/Trump/src/main/scala/model/Decks.scala)
Si è differenziato un `Deck` da uno `ShuffledDeck`, questo consente di rafforzare attraverso il type system il vincolo di gioco secondo il quale il deck deve essere mischiato prima di iniziare a giocare.

Per ridurre al minimo la duplicazione di codice (siccome un `ShuffledDeck` è sostanzialmente un Deck) si è utilizzata la typeclass `DeckOps` e polimorfismo ad-hoc, aggiungendo il fatto che `ShuffledDeck` è un alias opaco di `Deck` questo permette di utilizzare un'unica implementazione della logica di un `Deck` aggiungendo però questa differenziazione di tipi.

### Monade [`State`](/Trump/src/main/scala/statemonad/State.scala)
L'utilizzo della monade State è molto comodo per semplificare la scrittura di certe operazioni.

```scala
// Per esempio per pescare 3 carte è possibile fare
val dealTwoCards = for
    c1 <- deal()
    c2 <- deal()
    c3 <- deal()
yield (c1, c2, c3)
val cards = dealTwoCards.run(deck)._2
```

Sono stati effettuati dei cambiamenti alla monade State in modo da consentire non solo cambiamenti di stato ma anche cambiamenti nel tipo dello stato. Questo consente di preparare una computazione che inizia con un `Deck` e finise con uno `ShuffledDeck`.

```scala
// shuffleDeck: State[Deck, ShuffledDeck, Option[Card]]
val shuffleDeck =
    for
        _ <- shuffle[Deck]()
        c <- deal()
    yield (c)
// Questo permette di partire con un Deck, mischiarlo rendendolo uno ShuffledDeck e poi pescare una carta.

// Eseguendo questo è possibile quindi ottenere il nuovo deck mischiato dal quale è stata pescata una carta.
shuffleDeck.run(deck)
```

> **Nota:**
>
> E' purtroppo necessario specificare il parametro di tipo `[Deck]` in modo da disambiguare al compilatore l'utilizzo di `DeckOps[Deck]` rispetto a quello di `DeckOps[ShuffledDeck]` (che era invece stato importato). Altrimenti si sarebbe ottenuta una computazione del tipo `State[ShuffledDeck, ShuffledDeck, Option[Card]]`