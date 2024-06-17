# Implementazione

## Context
Context offre un'implementazione di default tramite `Context.apply` che non è altro che una case class.

Inoltre mette a disposizione dei metodi di utilità (come riferimenti diretti ad IO, Storage e deltaTimeNanos la conversione di quest'ultimo in secondi.) che possono anche essere aggiunti dall'utente finale attraverso delle *extensions*.
