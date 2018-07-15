Pattern-Searching

Run
---
java REcompile '<expression>' | java REsearch '<filename.txt>'

Variables
---
E = Expression
T = Term
F = Factor
v = Band of Literals

Grammar
...

Expression
---
E -> T
E -> T | E

Term
---
T -> F
T -> F*
T -> F+
T -> F?
T -> FT

Factor
---
F -> v
F -> (E)
F -> [alt]
F -> ![alt]!
F -> \
F -> .

Literals
---
v -> v
v -> v
