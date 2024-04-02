## Getting Started

Welcome to the language of Graphene, a purely functional language based on the architecture that underlies Google's open source languge, Carbon.

## Folder Structure

The workspace contains multiple folders by default, where:

- `documents`  : folder that has language specifications and implementation details.
- `programs`   : these are various examples of actual, runnable, Graphene code.
- `source`     : the folder to maintain sources and various scripts used to run source code.
- `source\src` : where source code is located.
- `tests`      : various Graphene test code used to debug source code.

Compiled output files will be generated in `bin`.

## General

As I work my way through building the compiler I will be uploading snapshots of it's completion at various stages. First will be the scanner, then the parser, semantic checker, and finally the code generation. This project was part of my final undergraduate research that was completed in a group setting. I would now like to revisit and work on parts of the compiler that, orginally, I never got to fully understand. 

> If you would like to run the compiler at it's various stages, in a console, run graphenes or graphenef in source, followed by a Graphene program of your choosing. The script graphenes will produce the output for tokens of the Graphene language, while graphenef will output true depending on if the program is valid or not.

In order to verify and test that my code is running properly I currently have a couple temporary scripts.
- `current_build` : Running 'current_build' followed by a Graphene program will compile and run the current version of Graphene on that program.
- `cb_programs` : Running 'cb_programs' will compile and run the current version of Graphene on all programs in the programs directory.
- `cb_tests` : Running 'cb_tests' will compile and run the current version of Graphene on all programs in the tests directory.

## To-Do

> `State desgin pattern` : Revisit the scanner to properly implement the [state design pattern](https://sourcemaking.com/design_patterns/state) pattern.

> `Parser error handling` : Revist the parser to properly implement errors that will need to be caught at this stage.

> `Next stage for parsing` : Modify table-driven parser to produce abstract syntax tree.

- 

## Bug Fixes / Updates

- `x==y` : Fixed an issue with the scanner in which an error was thrown any time a valid identifer `x` was next to the operator `==`.

- `fn main() -> boolean print()` : Because functions must be of a return type (In this case `boolean`.) a check for the parser was added to the bodies of functions in which a print expression can not be the only expression of a function body.

- `>, ?, #` : The scanner had issues with certain charaters unrecognized by the language and will now properly handle these chracters, as well as throw correct errors.

- `missing non-terminal rules` : A function of the parser handling rules of the [parse table](https://github.com/Frost0522/Graphene/tree/main/documents) was not checking for tokens of type 'equality' and 'less than' for non-terminal rules, 'simple expression tail' and 'term tail', resulting in errors being thrown.

- `issues with identifiers` : When naming an identifier, if you were to lead, or trail behind, with characters recognized as keywords, an early syntax error would be thrown. As a consequence, an extra check has been added to State_1.

- `updated parsing errors` : Previously, when specifying the type on a parameter, if the type was either misspelled or forgotten, an incorrect error would be thrown stating the function return type is undefined.