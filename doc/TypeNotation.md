## Type System Notation

### Design goal

The type notation is designed to minimize the ambiguity and typing effort of each expressed type.
Each part is strictly constructed from left to right implying a lexical meaning of "A of B".

### Notations

| Construction | Meaning                                         |
| ------------ | ----------------------------------------------- |
| T            | a type named T                                  |
| ?T           | a potentially uninitialized T                   |
| []T          | an in-place dynamic array of T                  |
| [N]T         | an in-place static array of T with N elements   |
| ref T        | a constant reference to T                       |
| mut T        | a mutable reference to T                        |
| T<A...>      | a specialized generic type T with argument A... |

### Built-in Types
#### Motivation
Provide the programmer otherwise inaccessible type that is supported by the hardware
#### Integers
The integer type notation is simple, being \[type notation\]\[size\]

The language support two types of integers, signed and unsigned, 
denoted respectively by 'i' and 'u'

The size notation is a number that is larger tha or equal to 8 and is a power of 2,
representing the number of bits contained in the integer. Though there are several exceptions:
'size' for representing the word-length, 'int' for an integer that is the largest size
that is garenteed to be fast(usually being the word length) and 'byte' for the smallest
addressable unit length of the machine (usually 8)
#### Floating point
By default we support floating point with IEEE 754 standard, 
which is half, single, double, extended precision denoted by f16, f32, f64, f80.
Be noted that for some systems without FPUs this will be emulated, and the size and
alignment is not garentted to follow the 'size' part after 'f'.





