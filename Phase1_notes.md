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

### Short circutit
- && stops at the first false part so put the null check first

### Return not found
- A method that returns an object returns null when nothing is found
- The caller handles null before using it and turns it into its own answer
- null means nothing there
- an empty collection means something there with zero results

### Public method calling a private overload
- The public signature can't change because the tests call it
- Have public calls a private version with an extra parameter
- Name the extra parameter differently than the field so it doesn't shadow it

### Encapsulation
- The class that owns the private data should own operations on it so the board copy is in ChessBoard
- private means private to the class so on ChessBoard can read another's arrays

### Shallow vs deep copy
- Copying only the outer array still shares the rows so changing the copy changes the original
- Deep copy builds a new board so nothing is shared
- Pieces can be shared because their fields are final so its immutable

### Testing hypothetical what if
- Copy the data and change the copy never the real board
- Make a fresh copy for every candidate move so tests don't pile up on each other
- validMoves = the piece's normal moves minus any move that leaves its own king in check
- Use the piece's own color for the check test not whose turn it is

### makeMove order
- Three ways a move is illegal are no piece, wrong team, not in validMoves
- All the checks come first then change the board then switch the turn last
- A failed move can not change anything
- Capturing is automatic because addPiece overwrites the square

### Checkmate and stalemate
- both ask if this team has any valid move
- different when the team is in check
- uses validMoves and not pieceMoves
- a move that leaves the king in check doesn't count
