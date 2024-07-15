# Design architetturale

È stato realizzato un engine che eseguirà il gameloop su tutti gli oggetti della scena attiva.

L'utente definise la logica del suo videogioco attraverso la composizione di comportamenti.

L'utente che utilizza il framework ne ha la seguente visione a strati:

|Layer|
|:----:|
|Editor testuale di gioco|
|Comportamenti built-in e definiti dall'utente|
|Sistema di IO|
|Motore di gioco|

## Editor
L'editor permette all'utente di definire le scene che compongono il suo gioco, gli oggetti presenti in queste scene e quali comportamenti gli oggetti acquisicono.

## Comportamenti
I comportamenti sono sostanzialmente le funzionalità che gli oggetti con quel comportamento acquisiscono. Questi implementano le funzionalità grazie alla definizione di metodi che vengono invocati dall'engine in momenti specifici del game loop (ad esempio quando l'oggetto viene creato oppure ad ogni nuovo frame).

L'utente può utilizzare i comportamenti già forniti dal framework oppure definirne dei propri.

## Sistema di IO
Viene messo a disposizione un concetto di IO per la gestione dell'interazione con il videogiocatore.

Ne verrà fornito uno built-in ma potrà anche essere sostituito dall'utente con uno custom.

## Motore di gioco
Il motore di gioco orchestra tutte le entità presenti in modo da generare l'output previsto. Inoltre fornisce delle API che permettono all'utente di interagirci (ad esempio per creare/distruggere oggetti, cercarne alcuni con specifici comportamenti, ecc....).
