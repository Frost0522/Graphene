## Getting Started

Welcome to the language of Graphene, a purely functional language based on the architecture that underlies Google's open source languge, Carbon.

## Folder Structure

The workspace contains multiple folders by default, where:

- `documents`  : folder that has language specifications and implementation details.
- `programs`   : these are various examples of actual, runnable, Graphene code.
- `source`     : the folder to maintain sources and various scripts used to run source code.
- `source\src` : where source code is located.
- `tests`      : various Graphene test code used to debug source code.

Meanwhile, the compiled output files will be generated in the `bin` folder by default.

> If you would like to see more on the full language specification as well, [this](https://www.cs.uni.edu/~wallingf/teaching/cs4550/compiler/specification.html) will provide further details.

## General

There are going to be multiple uploads as I work my way through the various stages of the compiler construction process. First will be the scanner (completed), then the parser (partially completed), semantic checker, and finally the code generation. More on these later. This project was part of my final undergraduate research that was done in a group. I would like to now look back on this experience and work on the parts of the compiler that, orginally, I never got to fully understand.

> If you would like to run the compiler in its current state, in a console, run graphenes or graphenef in source, followed by a Graphene program of your choosing. The script graphenes will produce the output for tokens of the Graphene language, while graphenef will output true or false depending on if the program is valid or not.

## To-Do

> `State desgin pattern` : Revisit the scanner to properly implement the [state design pattern](https://sourcemaking.com/design_patterns/state) pattern.

> `Parser error handling` : Revist the parser to properly implement errors that will need to be caught at this stage.

- `Next stage for parsing` : Modify table-driven parser to produce abstract syntax tree. 

## Bug Fixes

- `x==y` : Fixed an issue with the scanner in which an error was thrown any time a valid identifer `x` was next to the operator `==`.

- `fn main() -> boolean print()` : Because functions must be of a return type (In this case `boolean`.) a check for the parser was added to the bodies of functions in which a print expression can not be the only expression of a function body.

- `>, ?, #` : The scanner had issues with certain charaters unrecognized by the language and will now properly handle these chracters, as well as throw correct errors.

- `missing non-terminal rules` : A function of the parser handling rules of the [parse table](https://github.com/Frost0522/Graphene/tree/main/documents) was not checking for tokens of type 'equality' and 'less than' for non-terminal rules, 'simple expression tail' and 'term tail', resulting in errors being thrown.