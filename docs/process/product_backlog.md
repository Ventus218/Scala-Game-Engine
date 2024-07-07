# Product backlog
Done items should be moved to the bottom table

Priority goes from top to bottom.

|Id|Item|Details|
|--|----|-------|
|25|Inserire uno SwingRenderer per il testo||
|26|Inserire un behaviour built-in PositionFollower|L'oggetto che deve seguire prende in input un riferimento ad un behaviour e ad ogni frame settare la propria posizione (più un offset) a quella dell'oggetto seguito.|
|27|Inserire un behaviour built-in per un bottone|Sarà un rectrenderer con un campo text renderer che lavora tramite delega|
|28|Refactor: un unico Scalable generico||
|29|Refactor: I Renderer devono adottare il nuovo Scalable||
|30|Bugfix: Fare in modo che swingIO crei i canvas in modo lazy||
|31|Implementazione dei behaviour built-in **Velocity** e **Acceleration**||
|32|Refactor del framework per una migliore suddivisione in package||
|33|Refactor del framework per una migliore suddivisione in package||
|34|L'engine non deve essere runnabile più di una volta, aggiornare i test, e l'implementazione che deve lanciare delle eccezioni se necessario||
|35|Refactor: togliere tuple dove vengono passati x e y e mettere un tipo ad-hoc||
|13|Scelta e design dei videogiochi di esempio||
|12|Refactor e pulizia di TestUtils||
|14|Realizzazione dei videogiochi di esempio||
|36|Aggiornare diagramma modello del dominio||
|37|Fix: cache delle immagini scalate||


## Done

|Id|Item|Details|
|--|----|-------|
|1|Requisiti e specifica|Raccogliere i requisiti e definire in maniera formale la specifica del sistema|
|2|Preparare l'ambiente di sviluppo||
|3|Realizzazione scheletro e interfacce||
|6|Implementazione **SceneManager**|*Si è deciso che non era necessario da implementare*|
|15|Refactor Behaviour|e rimozione di GameObject|
|4|Specifica|Completare la sezione Specifica nella documentazione|
|5|Implementazione **Scene**||
|7|Implementazione **Storage**||
|9|Implementazione **Engine**||
|10|Implementazione behaviour built-in||
|11|Implementazione **IO (Swing)**|e behaviour correlati (Input e Renderer)|
|16|Refactor: Dimensionable -> Scalable||
|17|Refactor: Collider con più shape|Rettangolo e cerchio|
|18|Fix: Sovrapposizione di figure in IO|Quando due figure si sovrappongono fanno uno strano effetto grafico|
|19|Definizione priorità di rendering||
|20|Possibilità di settare un limite di FPS||
|21|Usare Set al posto di Seq per la creazione/distruzione di oggetti||
|22|Impedire l'esecuzione di run quando l'engine è già partito||
|23|Aggiungere un riferimento all'engine negli Handler di **SwingInputHandler**||
|24|Inserire un main complesso di test||