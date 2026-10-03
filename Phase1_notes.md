### Phase 1

#### ChessGame state
- ChessGame remembers state and uses pieceMoves
- Pick fields from what the getters return
- Constructor signature can't change so everything is built inside
- resetBoard is void

#### Shadowing
- A parameter with the same name as a field hides the field
- can't forget the this.variable

### Copy
- don't change the real variables and objects to check a hypothetical
- copy the data and change the copy
- That also means each potential change needs its own copy or tests would pile up on each other