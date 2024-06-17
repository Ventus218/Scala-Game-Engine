# Implementazione

## Context
Context offre un'implementazione di default tramite `Context.apply` che non è altro che una case class.

Inoltre mette a disposizione dei metodi di utilità (come riferimenti diretti ad IO, Storage e deltaTimeNanos la conversione di quest'ultimo in secondi.) che possono anche essere aggiunti dall'utente finale attraverso delle *extensions*.

## Scene
Scene ha una implementazione di default attraverso `Scene.apply`.

Il motivo per cui Scene prende in input una funzione che crea i game object e non direttamente i game object è che Scene è solo un template per gli oggetti e questi andranno creati solo quando la scena verrà caricata dall'engine.
