# Space Defender - Demo

## Made by
Michele Ravaioli

## Abstract
L'obbiettivo è la realizzazione di una demo di un gioco che utilizzi le varie funzionalità dello **ScalaGameEngine**.
Nel gioco **Space Defender** il giocatore controllerà una navicella spaziale con lo scopo di guadagnare più punti possibili, distruggendo con proiettili le navicelle nemiche.
Queste ultime lanciano a loro volta proiettili che possono danneggiare il player. La partita termina quando il player viene colpito danneggiato troppo dai colpi nemici.

## Tutorial
La navicella del giocatore si muove nella posizione del puntatore del mouse, e spara colpi periodicamente se viene premuto il tasto sinistro del mouse.
Il giocatore si può muovere solamente in un'area circoscritta, limitata alla zona inferiore dello schermo.

Il giocatore ha 5 punti vita. Questi vengono decrementati ogni volta che la navicella viene colpita da un proiettile nemico. Quando si perde un punto vita, la navicella
diventa invulnerabile per 2 secondi, per poi ritornare vulnerabile.

Il giocatore guadagna punti distruggendo le navicelle nemiche. Ogni navicella nemica ha un proprio punteggio e propri punti vita, e quando questa viene distrutta, viene sommato il suo punteggio
al punteggio totale della partita. 

Man mano che la partita procede, vengono creati nemici più resistenti e con diversi pattern di attacco. L' obiettivo del gioco è guadagnare più punti possibili.

## Implementazione

### Timer
Essendo presenti molti comportamenti basati sull'attendere un certo intervallo di tempo o sull'eseguire delle azioni più avanti nel tempo, si è deciso di implementare una monade che nascondesse
questa meccanica time-dependent. Il trait **Timer** definisce operazioni come `map`, `flatMap` o `foreach` che vengono applicate allo stato attuale del timer solo se si è verificata una certa condizione temporale,
dipendente dall'implementazione di **Timer**. Per poter aggiornare lo stato interno del timer, è necessario chiamare `update(dt)`, che restituisce un nuovo timer aggiornato basandosi sull'intervallo di tempo passato in input.

I timer disponibili sono principalmente tre: un timer che è attivo solo dopo una certo periodo di tempo (`runAfter`), un timer che si attiva solo per una volta dopo un certo periodo di tempo (`runOnceTimer`),
e un timer che si attiva periodicamente a ongi intervallo di tempo fissato (`runEvery`).

### TimerStateMachine
Implementa una macchina a stati finiti basata sui timer. Definito lo stato iniziale, lo stato cambia nel tempo secondo le regole definite dall'utente in `onStateChange`, una funzione che dato uno stato in input
restituisce il nuovo stato in cui entrare e per quanto tempo bisogna rimanere in quello stato. Le azioni da compiere mentre si è in uno stato sono definite sempre dall'utente in `whileInState`.

Questo trait nasconde all'utente l'implementazione dell'engine e del game loop e fornisce funzionalità (`onStateChange` e `whileInState`) per modellare in maniera funzionale il comportamento di un oggetto di gioco.

### EntityStateMachine
Un'estensione di **TimerStateMachine**, che implementa e racchiude i comportamenti comuni alle entità dinamiche di gioco come il player e i nemici. Lavora su stati `EntityState`, e implementa il comportamento per gli stati `Spawinig` e `Dying`.

### Health, Invulnerability
**Health** rappresenta i punti vita. Fornisce i metodi `hit(damage)` per rimuovere punt vita e `kill()` per azzerarli. Fornissce i metodi `onHit` e `onDeath` per eseguire azioni quando si verifica quel particolare evento.

**Invulnerability** è un'estensione di **Health** che permette di non essere danneggiati se `invulnerability` é _true_.

### Bullet
Rappresenta un proiettile. Ogni proiettile ha un proprio dannno `damage` e un set ti bersagli `targets`. Se il proiettile entra in collisione con un suo bersaglio (deve avere il behaviour **CircleCollider**), allora viene chiamata la `hit` sull'entità colpita (deve avere il trait **Health**)
e la `onTargetHit`.

I proiettili possono essere **PlayerBullet**, **EnemyBullet** o **EnemyLaser**.

### Score
Rappresenta un punteggio, associato ai nemici.

### Player
E' una **EntityStateMachine** che rappresenta la navicella del giocatore. Si può muovere con il puntatore del mouse, e mentre è premuto il tasto sinitro del mouse, viene sparati 5 proiettili al secondo.
Il player ha 5 punti vita, e se il player muore la partita termina. **Player** ha due stati: `Normal` e `Hurt`. Lo stato iniziale è `Normal`, e se il viene danneggiato da un proiettile passa allo stato `Hurt` per 2 secondi, per poi ritornare a `Normal`. 
Mentre è in stato _Hurt_, rimane invulnerabile.

### Enemy
Trait che rappresenta un nemico. Funge da factory per i nemici, che possono essere: **Dropper**, **Ranger**, **Turret**, **Beacon**.

#### Dropper
E' una **EntityStateMachine** che rappresenta una navicella nemica con 3 punti vita e uno score di 10. Deriva dalla classe **AbstractDropper**.
Quando viene spawnato, esegue ripetutamente le seguenti azioni:
1. Si muove orizzontalmente per un intervallo di tempo casuale tra 1 e 2 secondi.
2. Si ferma e spara un proiettile verso il basso. Rimane fermo per 0.7 secondi.

#### Ranger
E' una **EntityStateMachine** che rappresenta una navicella nemica con 5 punti vita e uno score di 20.
Quando viene spawnato, esegue ripetutamente le seguenti azioni:
1. Si muove rapidamente in 3 posizioni casuali.
2. Si ferma per 1 secondo.
2. Spara una raffica di 3 proiettili in direzione del player.

#### Turret
E' una **EntityStateMachine** che rappresenta una navicella nemica con 15 punti vita e uno score di 50.
Quando viene spawnato, esegue ripetutamente le seguenti azioni:
1. Ruota lentamente su se stesso, nel mentre spara in tutte le direzioni 4 proiettili lenti ogni 1.3 secondi.

#### Beacon
E' una **EntityStateMachine** che rappresenta una navicella nemica con 10 punti vita e uno score di 100. Deriva dalla classe **AbstractDropper**.
Quando viene spawnato, esegue ripetutamente le seguenti azioni:
1. Si muove lentamente in orizzontale per un intervallo di tempo casuale tra 2.5 e 4 secondi.
2. Si ferma e spara laser verso il basso. Rimane fermo per 2 secondi.

### EnemySpawner
Trait che definisce la logica di creazione dei nemici. L'implementazione fornita è **EnemySpawnerTimer**, che deriva da **TimerStateMachine** e spawna i nemici secondo dei timer prefissati e secondo la fase in cui si trova.

### GameConstants
**GameConstants** contiene tutte le impostazioni globali per il gioco, come la dimensione dello schermo, della scena, impostazioni predefinite per la UI ecc.

### GameManager
Object singleton che governa e gestisce il gioco e le sue fasi. Deriva da **TimerStateMachine**.
All'avvio della scena di gioco, questo esegue in ordine:
1. Crea il player, e mostra punteggio e vita.
2. Mostra il testo "Mission Start!".
3. Nascondi il testo "Mission Start!".
4. Avvia l'**EnemySpawner**.
5. Quando il player muore, passa alla scena di game over.

### SceneManager
Singleton che fornisce le tre scene del gioco. Le scene sono `menuScene`, la scena del menu iniziale, `gameScene`, la scena di gioco, e `gameoverScene`, la scena di game over.

### UI
Sono state create delle classi per facilitare la creazione dell'interfaccia, come **GameButton** per i pulsanti, **TwoLineText** per i testi su due righe, e tutte le implementazioni
per istanziare più agevolmente gli oggetti.

### VectorUtils
Classe di utility che aggiunge operazioni sui **Vector2D**, come la normalizzazione, il modulo, il clamping e l'interpolazione lineare.