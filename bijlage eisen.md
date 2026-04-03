## Algemene eisen

| ID   | Omschrijving                             | Prio | Punten | Status |
|------|------------------------------------------|------|--------|--------|
| AL01 | Packagestructuur behouden                | Must | 0      | v      |
| AL02 | Build met Maven + OpenJDK 13             | Must | 0      | v      |
| AL03 | Code netjes en onderhoudbaar             | Must | 0      | v      |
| AL04 | Eigen werk + kennis compilerarchitectuur | Must | 0      | v      |

---

## Parseren

| ID   | Omschrijving                             | Prio | Punten | Status |
|------|------------------------------------------|------|--------|--------|
| PA00 | Gebruik eigen generieke stack            | Must | 0      | v      |
| PA01 | Parser level 0 (basis ICSS)              | Must | 10     | v      |
| PA02 | Parser level 1 (variabelen)              | Must | 10     | v      |
| PA03 | Parser level 2 (expressies + prioriteit) | Must | 10     | v      |
| PA04 | Parser level 3 (if/else)                 | Must | 10     | v      |
| PA05 | Minimaal 30 punten PA01–PA04             | Must | 0      | v      |

---

## Checken

| ID   | Omschrijving                            | Prio   | Punten | Status |
|------|-----------------------------------------|--------|--------|--------|
| CH00 | Minimaal 4 checks geïmplementeerd       | Must   | 0      | v      |
| CH01 | Geen ongedefinieerde variabelen         | Should | 5      | v      |
| CH02 | Typecontrole bij operaties              | Should | 5      | v      |
| CH03 | Geen kleuren in operaties               | Should | 5      | v      |
| CH04 | Correcte types bij declaraties          | Should | 5      | v      |
| CH05 | If-conditie is boolean                  | Should | 5      | v      |
| CH06 | Variabelen alleen binnen scope gebruikt | Must   | 5      | v      |

---

## Transformeren

| ID   | Omschrijving                           | Prio | Punten | Status |
|------|----------------------------------------|------|--------|--------|
| TR01 | Evaluatie van expressies naar literals | Must | 10     | v      |
| TR02 | Evaluatie van if/else                  | Must | 10     | v      |

---

## Genereren

| ID   | Omschrijving               | Prio | Punten | Status |
|------|----------------------------|------|--------|--------|
| GE01 | Generator naar CSS2 string | Must | 5      | v      |
| GE02 | Correcte inspringing       | Must | 5      | v      |

---

## Eigen Uitbreidingen

| ID | Omschrijving                                                  | Prio  | Punten | Status |
|----|---------------------------------------------------------------|-------|--------|--------|
| -  | Delen door implimenteren met. (delen door 0 wordt opgevangen) | could | 10     | v      |
| -  | Extra test tegeovoegd                                         | could | 1      | v      |