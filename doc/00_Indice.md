# Scala Game Engine - Documentazione

## Autori
- Alessandro Venturini
- Michele Ravaioli
- Corrado Stortini

## Abstract
Si vuole realizzare un semplice game engine.
Inoltre verranno realizzati almeno due giochi diversi, per dimostrare che il framework sia abbastanza general purpose.

Le caratteristiche di base che verranno fornite sono le seguenti:
- Game loop
- Supporto a più scene
- Input e output
- Design che favorisca l'implementazione all'utilizzatore dei propri oggetti di gioco e relativi comportamenti
- Componenti built-in pronti all'uso

L'intenzione è quella di utilizzare le seguenti caratteristiche peculiari di scala:
- Mixin per realizzare quello che normalmente viene implementato con il pattern Component. In modo da fornire una esperienza di sviluppo più intuitiva.
- DSL per definire la struttura del gioco, delle scene e degli oggetti di gioco. Questo DSL avrà lo scopo di sostituire un editor grafico con un approccio dichiarativo.

## Indice
- [Processo di sviluppo](./01_ProcessoSviluppo.md)
- [Requisiti e specifica](./02_RequisitiSpecifica.md)
- [Design architetturale](./03_DesignArchitetturale.md)
- [Design di dettaglio](./04_DesignDettaglio.md)
- [Implementazione](./05_Implementazione.md)
- [Testing](./06_Testing.md)
- [Retrospettiva](./07_Retrospettiva.md)