# Requisiti e specifica

## Requisiti di business
- Si vuole realizzare un game engine in Scala per poter sperimentare come le funzionalità avanzate di questo linguaggio possono influenzare l'esperienza di sviluppo.

- Il game engine deve essere abbastanza general purpose.
- Deve essere anche semplice da utilizzare.
- Il software deve essere progettato e sviluppato per poter essere evoluto nel tempo.
- Per verificare la buona riuscita del progetto deve essere possibile realizzare almeno due giochi con caratteristiche differenti.

## Modello del dominio
Si è presa ispirazione da altri game engine esistenti e di successo.

Il framework avrà le seguenti caratteristiche:
- Game loop
- Più scene (*scene*)
- Più game object
- Ogni game object ha un comportamento (*behaviour*)
- I behaviour sono componibili tra di loro
- Devono essere presenti dei behaviour built-in ad esempio per gestire l'input o il rendering grafico
- L'editor sarà testuale e sfrutterà un DSL per la definizione delle scene

![Diagramma delle classi - modello del dominio](./img/Modello%20del%20dominio.png)
