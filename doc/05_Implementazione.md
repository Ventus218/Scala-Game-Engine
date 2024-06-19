# Implementazione

## Engine
L'engine ha un'implementazione di default attraverso `Engine.apply()` che accetta come parametri una IO e uno Storage.

### Game Loop (run() e stop())
Una volta avviato il game loop attraverso il metodo `engine.run()`, il game loop per prima cosa chiamerà gli handler dei behaviors nel seguente ordine:

    - onInit
    - onEnabled (solo sui behaviours abilitati)
    - onStart (solo sui behaviours abilitati)
    Loop until stopped
        - onEarlyUpdate (solo sui behaviours abilitati)
        - onUpdate (solo sui behaviours abilitati)
        - onLateUpdate (solo sui behaviours abilitati)
    -onDeinit

Chiamando il metodo `engine.stop()` l'engine capirà che si deve fermare ed una volta finito l'attuale ciclo (quindi dopo aver chiamato la onLateUpdate sui gameObjects abilitati) uscirà da esso per chiamare la onDeinit su tutti i gameObjects

### Delta time nanos
L'engine offre la possibilità di ricavare il tempo trascorso dallo scorso frame al frame corrente attraverso `engine.deltaTimeNanos`.

## Scene
Scene ha una implementazione di default attraverso `Scene.apply`.

Il motivo per cui Scene prende in input una funzione che crea i game object e non direttamente i game object è che Scene è solo un template per gli oggetti e questi andranno creati solo quando la scena verrà caricata dall'engine.

## Built-in behaviours

Di seguito sono descritte le implementazioni dei vari Behaviours built-in del SGE.
Da notare che ogni behaviour built-in è un mixin di Behaviour.

### Identifiable

Un oggetto che viene mixato con il behaviour **Identifiable** avrà a disposizione un id e potrà essere cercato attraverso questo tra tutti gli altri oggetti.

### Positionable

Quando un behaviour usa **Positionable** come mixin, avrà accesso ad una x ed una y settate a 0 di default.
Si possono passare i valori iniziali di x e y se non si vuole inizializzarle a 0 e si possono cambiare una volta creato il behaviour.

*Esempio*
```scala
// create a Positionable with x = 5, y = 0 then change y to 3
val positionable: Positionable = new Behaviour() with Positionable(5)
positionable.y = 3
```
