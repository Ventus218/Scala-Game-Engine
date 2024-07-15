# Processo di sviluppo

Si è deciso che tutti e 3 gli sviluppatori saranno anche stakeholder / committenti del progetto.
Questo in quanto l'intenzione è quella di realizzare un framework e non un normale programma eseguibile e inoltre tutti gli sviluppatori hanno già esperienza nell'ambito del game development.

Il product owner è stato scelto dal gruppo ed è Alessandro Venturini.

Si sono realizzati due file sotto la cartella [process](process/) ovvero:
- [Product backlog](process/product_backlog.md)
- [Sprint backlog](process/sprint_backlog.md)

Si è inoltre deciso che lo sprint backlog manterrà una storia di tutte le sprint realizzate.

## Sprint
Le sprint dureranno una settimana ciascuna e ci si aspetta un quantitativo effettivo di lavoro da parte di ogni sviluppatore di circa 15 ore.

### Meeting
- Un incontro a inizio sprint per decidere come suddividere agli sviluppatori le task
- Per conciliare gli impegni degli studenti si è deciso di utilizzare un gruppo whatsapp per comunicare giornalmente gli avanzamenti al posto del classico meeting ad inizio giornata
- Un incontro a fine sprint per valutare il risultato e applicare eventuali correzioni al processo di sviluppo

## La nostra definizione di "done"
- tutti i test devono essere superati
- l'intero progetto deve compilare
- documentazione inline per tutti i metodi/classi/ecc.. pubblici
- la documentazione della repository deve essere aggiornata
- il software deve rispettare certe caratteristiche di qualità
  - deve essere formattato in maniera consistente

## Continuous integration
Per migliorare l'agilità del team si vuole sfruttare la continuous integration per quanto riguarda testing, documentazione e delivery.

### Branching model
Verrà utilizzato il modello Git-Flow.

Per il nostro caso sembra non sarà particolarmente utile l'utilizzo di *release branch* ma nel caso in cui dovesse diventarlo allora verranno utilizzate.

### Peer review
Ogni pull request (su main o develop) deve essere approvata da almeno un altro componente del team prima che se ne possa fare il merge.

### Github branch protection rules
Per rafforzare il processo di lavoro, si è applicata la seguente regola sui branch *main* e *develop*:

È possibile effettuare merge solo a seguito di una pull request con test verdi e almeno una revisione da parte di uno degli sviluppatori.

### Delivery
Si sono realizzati due workflow di delivery:
- Pubblicazione della documentazione sotto forma di GitHub Pages ([qui](/.github/workflows/deploy-gh-pages.yml) il file di configurazione)
- Creazione di una release a seguito di nuovi tag sul branch main. Con annessa costruzione e pubblicazione dei fat JAR ([esempio](https://github.com/Ventus218/PPS-23-SGE/releases/tag/1.1.1)). ([qui](/.github/workflows/delivery.yml) il file di configurazione)
