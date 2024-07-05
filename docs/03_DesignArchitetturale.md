# Design architetturale

È stato realizzato un engine che eseguirà il gameloop su tutti gli oggetti della scena attiva.

L'utente definise la logica del suo videogioco attraverso la composizione di comportamenti.

L'utente che utilizza il framework ne ha la seguente visione a strati:

|Layer|
|:----:|
|Editor testuale di gioco|
|Comportamenti built-in e definiti dall'utente|
|Motore di gioco|

## Editor
L'editor permette all'utente di definire le scene che compongono il suo gioco, gli oggetti presenti in queste scene e quali comportamenti gli oggetti acquisicono.

## Comportamenti
I comportamenti sono sostanzialmente le funzionalità che gli oggetti con quel comportamento acquisiscono. Questi implementano le funzionalità grazie alla definizione di metodi che vengono invocati dall'engine in momenti specifici del game loop (ad esempio quando l'oggetto viene creato oppure ad ogni nuovo frame).

L'utente può utilizzare i comportamenti già forniti dal framework oppure definirne dei propri.

## Motore di gioco
Il motore di gioco orchestra tutte le entità presenti in modo da generare l'output previsto. Inoltre fornisce delle API che permettono all'utente di interagire (ad esempio per creare/distruggere oggetti, cercarne alcuni con specifici comportamenti, ecc....).
Inoltre il motore di gioco mette a disposizione un concetto di IO che permette all'utente di definire il proprio sistema di input/output (grafico o meno).
