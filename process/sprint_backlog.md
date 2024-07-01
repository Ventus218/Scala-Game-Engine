# Sprint backlog

## Sprint 0 (inizio 30/05/2024)
> **Nota**
> Per questa prima sprint si è deciso di lavorare in gruppo in quanto tutti gli sviluppatori sono anche stakeholders e di conseguenza devono lavorare insieme per prendere le decisioni iniziali per il progetto.

|Backlog item Id|Sprint task|Assignee|Not started|Work in progress|Done|
|------------|-----------|------------|:-:|:-:|:-:|
|1|Requisiti di business|Tutti|||X|
|1|Requisiti modello di dominio|Tutti|||X|
|1|Requisiti funzionali utente / di sistema|Tutti|||X|
|1|Requisiti non funzionali|Tutti|||X|
|1|Requisiti di implementazione|Tutti|||X|
|2|Prima build|Tutti|||X|
|2|Setup framework di testing|Tutti|||X|
|2|Setup branching model (Git-Flow)|Tutti|||X|
|2|Setup CI per testing|Tutti|||X|
|2|Setup CI per delivery|Tutti|||X|
|3|Gettare basi architettura|Tutti|||X|
|3|Definizione delle interfacce base|Tutti|||X|

## Sprint 1 (inizio 16/06/2024)
|Backlog item Id|Sprint task|Assignee|Not started|Work in progress|Done|
|------------|-----------|------------|:-:|:-:|:-:|
|4|Specifica|Tutti|||X|
|15|Refactor Behaviour|Tutti|||X|
|5|Implementazione **Scene**|Alessandro|||X|
|6|Implementazione **SceneManager**|Alessandro|||X|
|7|Implementazione **Storage**|Alessandro|||X|
|9|Implementazione **Engine** (game loop)|Corrado|||X|
|9|Implementazione **Engine** (metodi find...)|Alessandro|||X|
|9|Implementazione **Engine** (altri metodi (caricamento scena, distruzione/creazione))|Michele|X|||
|10|Behaviour **Positionable**|Corrado|||X|
|10|Behaviour **Dimensionable**|Corrado|||X|
|10|Behaviour **Collider** (di diverse forme)|Corrado|||X|
|11|Implementazione **IO (Swing)**|Michele|||X|
|11|Implementazione **SwingRendererB**|Michele|||X|
|11|Implementazione **SwingInputB**|Alessandro||X||

## Sprint 2 (inizio 24/06/2024)
|Backlog item Id|Sprint task|Assignee|Not started|Work in progress|Done|
|------------|-----------|------------|:-:|:-:|:-:|
|9|Implementazione **Engine** caricamento scena e distruzione/creazione|Michele|||X|
|9|Implementazione **Engine** conversione delta time in secondi|Alessandro|||X|
|9|Implementazione **Engine** abilitazione e disabilitazione degli oggetti|Alessandro|||X|
|11|Implementazione **SwingInputHandler** (old SwingInputB)|Alessandro|||X|
|16|Refactor: Dimensionable -> Scalable|Corrado|||X|
|17|Refactor: Collider con più shape - Rettangolo|Corrado|||X|
|17|Refactor: Collider con più shape - Cerchio|Corrado|||X|
|18|Fix: Sovrapposizione di figure in IO|Michele|||X|
|19|Definizione priorità di rendering|Michele|||X|
|20|Possibilità di settare un limite di FPS|Alessandro|||X|
|21|Usare Set al posto di Seq per la creazione/distruzione di oggetti|Tutti|||X|
|22|Impedire l'esecuzione di run quando l'engine è già partito|Tutti|||X|
|23|Aggiungere un riferimento all'engine negli Handler di **SwingInputHandler**|Tutti|||X|
|24|Inserire un main complesso di test|Tutti|||X|

## Sprint 3 (inizio 1/07/2024)
|Backlog item Id|Sprint task|Assignee|Not started|Work in progress|Done|
|------------|-----------|------------|:-:|:-:|:-:|
|25|Inserire uno SwingRenderer per il testo|Michele|X|||
|26|Inserire un behaviour built-in PositionFollower|Corrado|||X|
|27|Inserire un behaviour built-in per un bottone|Alessandro|X|||
|28|Refactor: un unico Scalable generico|Corrado|||X|
|29|Refactor: I Renderer devono adottare il nuovo Scalable|Michele|X|||
|30|Bugfix: Fare in modo che swingIO crei i canvas in modo lazy|Michele|X|||
|13|Scelta e design dei videogiochi di esempio|Tutti|X|||
|12|Refactor e pulizia di TestUtils|Alessandro|X|||
