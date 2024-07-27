## Getting Started

Welcome to the language of Graphene, a purely functional language based on the architecture that underlies Google's open source languge, Carbon.

## Folder Structure

The workspace contains multiple folders by default, where:

- `bin\src`    : location of compiled code
- `documents`  : folder that has language specifications and implementation details.
- `programs`   : .gr files containing various examples of Graphene code.
- `source`     : contains build scripts for Windows and Linux.
- `source\src` : where source code is located.
- `tests`      : various Graphene test code used in debugging.

## How to build Graphene

In a terminal, navigate to the source directory and run the build command. Once compiled there will be serveral usable commands.

- `build -c /path_to_program/program_name'  : runs the most recently compiled source code
- `build -gs /path_to_program/program_name' : 'graphenes' runs code for the scanner and outputs all tokens
- `build -gf /path_to_program/program_name' : 'graphenef' executes the parser, before node implementation, and either throws 
                                              an error or outputs true if the program is valid
- `build -gp /path_to_program/program_name' : 'graphenep' is the parser, after node implementation, that uses an object to print out 
                                              the node stack

## To-Do

> `State desgin pattern` : Revisit the scanner to properly implement the [state design pattern](https://sourcemaking.com/design_patterns/state) pattern.

> `Parser error handling` : Revist the parser to properly implement errors that will need to be caught at this stage.

> `Next stage for parsing` : Modify table-driven parser to produce abstract syntax tree.

## Bug Fixes / Updates

- `x==y` : Fixed an issue with the scanner in which an error was thrown any time a valid identifer `x` was next to the operator `==`.

- `fn main() -> boolean print()` : Because functions must be of a return type (In this case `boolean`.) a check for the parser was added to the bodies of functions in which a print expression can not be the only expression of a function body.

- `>, ?, #` : The scanner had issues with certain charaters unrecognized by the language and will now properly handle these chracters, as well as throw correct errors.

- `missing non-terminal rules` : A function of the parser handling rules of the [parse table](https://github.com/Frost0522/Graphene/tree/main/documents) was not checking for tokens of type 'equality' and 'less than' for non-terminal rules, 'simple expression tail' and 'term tail', resulting in errors being thrown.

- `issues with identifiers` : When naming an identifier, if you were to lead, or trail behind, with characters recognized as keywords, an early syntax error would be thrown. As a consequence, an extra check has been added to State_1.

- `updated parsing errors` : Previously, when specifying the type on a parameter, if the type was either misspelled or forgotten, an incorrect error would be thrown stating the function return type is undefined.