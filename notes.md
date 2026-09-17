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