# Stealth Game - Demo

## Made by
Corrado Stortini

## Abstract
L'obbiettivo è la realizzazione di una demo di un gioco che vadi ad utilizzare varie funzionalità dello **ScalaGameEngine**.
Nel gioco **Stealth Game** il giocatore controllerà il personaggio protagonista e lo muoverà affinchè esso non venga scoperto dalle guardie, le quali potranno avere diversi pattern di movimento.
Il gioco consiste in tre livelli, e il giocatore per passare al livello successivo e, infine, vincere, dovrà raggiungere le scale.
Se invece il personaggio dovesse entrare nel raggio visivo di una guardia, esso rinizierà il livello attuale, fino ad un numero di volte che dipende dalla difficoltà.
- `Facile`: 5 vite
- `Normale`: 3 vite
- `Difficile`: 1 vita
- `Impossibile`: 1 vita
Ogni volta che il personaggio supera un livello guadagna una vita, ad eccezione della difficoltà `Impossibile`.

## Tutorial
Per muovere il personaggio si usano i tasti `W`, `A`, `S`, `D`, rispettivamente per muoverlo verso sopra, sinistra, sotto e destra.
Tenendo premuto `Spazio` si può "sprintare", ovvero aumentare la velocità del proprio personaggio.

## Implementazione

### Config
**Config** contiene tutte le impostazioni globali per il gioco, come la dimensione dello schermo, della scena, dei personaggi ecc.

### Action
**Action** è un enum con tre case `IDLE`, `MOVE`, `SPRINT`, che rappresentano rispettivamente lo stare fermo, il muoversi e il correre.

### Direction
**Direction** è un enum con quattro case che rappresentano la direzione: `TOP`, `LEFT`, `BOTTOM`, `RIGHT`.

### Movement
**Movement** è un tipo opaco che rappresenta una tupla `(Action, Direction)`.

### MovementState
**MovementState** è una monade che si occupa di gestire il cambio di stato a partire da un **Movement**.
**MovementState** da a disposizione vari metodi per poter girare la direzione del movimento a destra o sinistra, muoversi, correre, fermarsi e ovviamente prendere la **Direction** o **Action** attuale.

### Character
**Character** è la classe astratta che **Player** e **Enemy** estendono.
Essa si occupa di aggiornare la velocità ad ogni update del game loop in base alle varie collisioni con i muri (facendo in modo che il personaggio non vada a oltrepassare un muro) e al suo `movement`. `movement` è di tipo **Movement** e rappresenta il movimento attuale del **Character**.

### Player
**Player** è la classe che rappresenta il personaggio controllato dal giocatore.
Accetta come parametri in ingresso al costruttore `currentScene` e `nextScene` entrambi di tipo **Scene** così da poter cambiare scena in caso di collisione con le scale o resettare la scena corrente in caso di collisione con i raggi visivi dei nemici.
Utilizza inputHandler per gestire il movimento. Gli handler che si occupano di muovere **Player** vanno quindi ad aggiornare la `movement` attraverso i metodi dati a disposizione da **Character**.

### Enemy
**Enemy** è la classe che rappresenta qualsiasi tipo di nemico.
Appena esso viene creato nella scena, creerà a sua volta il suo raggio visivo.
Durante tutto il suo ciclo di vita, si occuperà di aggiornare dimensioni e offset del raggio visivo in modo che esso lo segua correttamente in base alla direzione di **Enemy**.
Per esempio se il nemico guarda verso il basso, il raggio visivo dovrà avere un offset che lo porti a stare sotto ad **Enemy**, viceversa se guarda verso l'alto.
bisogna swappare le dimensioni del raggio visivo quando il nemico si gira verso destra o sinistra, poichè, se il raggio ha una certa altezza quando il nemico guarda in basso, essa dovrà diventare la larghezza del raggio quando il nemico si gira verso sinistra o destra.

### Patterns
Sono stati realizzati vari **Behaviour** che fungono da "Pattern" di movimento per i nemici.

#### OnCollidePattern
**OnCollidePattern** è un trait privato da cui ereditano tutti quei pattern che eseguono una azione una volta avvenuta la collisione del raggio visivo dei nemici contro un muro. Per esempio **TurnLeftOnCollidePattern** fa girare a sinistra il nemico se esso guarda un muro.

**StopOnCollidePattern**, anch'esso privato, estende **OnCollidePattern** così da permettere ad un nemico di fermarsi per un tot di secondi dopo aver visto un muro e solo allora riprendere il suo cammino. Cosa fare una volta passati i secondi passati in ingresso (`nSecondsToWait`) lo decidono i trait che estendono **StopOnCollidePattern**, per esempio **StopThenTurnLeftCollidePattern**, una volta passati i secondi necessari, gira il nemico a sinistra e lo fa muovere.

#### MovingPattern
**MovingPattern** si occupa di far muovere un nemico, senza specificare cambi di direzione.
Esso estende **OnCollidePattern**, questo perchè ci si aspetta che se un nemico si muove, allora esso vuole gestire il caso in cui guarda un muro.

#### TurningPattern
**TurningPattern** è un trait privato, e le sue estensioni **TurningLeftPattern** e **TurningRightPattern** prendono in ingresso un numero di secondi che rappresentano ogni quanto tempo il nemico si deve girare rispettivamente verso sinistra o destra.

### Level
**Level** rappresenta la scena che tutti i livelli doveno avere, per esempio **Player**, le scale, il testo che mostra la vita del giocatore, e i confini della scena.

### StartingMenu
**StartingMenu** mostra un background con due tasti, `Play` per scegliere la difficoltà e `Close` per chiudere il gioco.

### DifficultyMeny
**DifficultyMenu** mostra lo stesso background di **StartingMenu** e da la possibilità di tornare indietro al menù iniziale o di scegliere la difficoltà. Una volta scelta la difficoltà, verrà caricato il primo livello e il gioco inizierà.

### LoseGame e WinGame
**LoseGame** e **WinGame** rappresentano rispettivamente il menù di sconfitta e quello di vittoria. L'unica differenza tra queste due scene e la **StartingMenu** è una scritta in overlay che indica se il giocatore ha vinto oppure ha perso.

## Immagini
Tutte le immagini sono state scaricate gratis da Freepik.