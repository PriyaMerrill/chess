# My Project Notes
##### Ideas I come across while working

### Phase 0:

#### Constructors and Fields

- A constructor's parameters are just local values, they disappear when the constructor returns unless you save them
- this.parameter = .. copies the parameter into a field so the object carries that value for the rest of its life
- Getters just give back what is stored in the field

#### Mutable Objects as hash keys***

- HashMap / HashSet computes hashCode() once!! (when the object is inserted) to pick a bucket
- If the object's fields change after then its hashCode() changes too. But the collection does not move it to a new bucket
- Two now different objects can even collide and look equal when they're not which corrupts the collection
- Fix: make objects used as hash keys immutable like with final fields set in the constructor. Then their hash code can never get lost after insertion

#### equals() / hashCode() basics

- equals() is inherited from Object so overriding it must keep the exact signature equals(Object o), a ChessPosition typed parameter would just add a new method instead of override. HashMap / Collections would ignore it
- equals (Object o) must handle o == null -> false (never throw) and confirm o is actually the right type before casting 
- After the check it is safe to cast o down to the real type and compare fields
- hashCode() should combine the same fields equals() compares so equal objects always hash the same

#### equals()/hashCode() contract

- The rule is one way. Equal objects must have equal hash codes
- not reversed. Unequal objects can share a hash code
- A hashCode() that ignores a field that equals() uses is still legal because objects that are truly equal will still share that field
- The cost of this is performance since more objects collide in the same bucket. HashMap / HashSet lookups become linear scan instead of instant

#### Enums vs regular objects in equals()

- Enum constants (in this ex: Queen, Rook, etc.) are guaranteed to exist as exactly one single object for the whole program. Java never creates a second copy of PieceType.QUEEN
- So == is always safe and correct on an enum. It's checking identity not value but there is only one object to be identical to
- == on enums is also null safe
- Regular classes don't have this guarantee. two separately new objects can represent the same value so == checks the wrong thing (identity not value). Objects.equals()/.equals() is used instead

#### Composing equals() / hashCode() across nested objects

- When a class holds fields that are themselves objects with their own correct equals() don't rederive the field by field comparison. Use that object's own equals()
- getting .equals() right first matters because other classes will build on top of it instead of duplicating it sometimes

#### 2D arrays

- Type[][] name = new Type[...][...]; an array of arrays
- Access with name[...][...]
- Object type arrays are automatically null so no extra free empty spot logic is needed

#### 1-indexed vs 0-indexed coordinates

- ChessPosition rows and columns went from 1-8
- array indexes are 0-7
- Convert with variable-1
- Use that conversion only when indexing directly into the array: doing this with addPiece and getPiece

#### @BeforeEach and test fails
- A @BeforeEach method runs before every @Test in the class
- this is why when I was testing the first parts all tests were failing
- if the shared setup throws every test in the class fails even if the code is right

#### Arrays and equals() / hashCode()
- Arrays don't override equals() so comparing two arrays checks object identity and not the contents
- two arrays with identical values but different objects would come back false
- In a 2D array use "deep"
- Arrays.deepEquals(a, b)
- Regular Arrays.equals() only compares one level deep so its not enough for 2D

Interfaces
- An interface is like a contract. A class that implements it writes the method body
- Can hold a static method with a body for shared helper logic
- static interface methods aren't inherited so call it through name of interface