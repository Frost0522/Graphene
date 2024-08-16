## Getting Started

Welcome to Graphene, a purely functional language based on a small subset of Google's open source language, Carbon.

## Folder Structure

- `bin\src` : Location of compiled code.
- `documents` : Folder that has language specifications and implementation details.
- `programs` : .gr files containing various examples of Graphene code.
- `source` : Contains build scripts for Windows and Linux.
- `source\src` : Where source code is located.
- `tests` : Various Graphene test code used in debugging. 

## How to build Graphene

In a terminal, navigate to the source directory and run the build command.

- `build` : This is how you compile source code and generate scripts to run Graphene.
   - `build -reload` : Reloads main build script and updates any changes to graphene scripts

- `graphene path_to_file/file_name` : Most recent compilation of the source code.
    - `graphene -s path_to_file/file_name` : Most recent compilation of only the scanner's output (Prints the tokens and their types).
    - `graphene -f path_to_file/file_name` : Most recent compilation of only the parser's output (Validates Graphene files).
    - `graphene -p path_to_file/file_name` : Most recent compilation of only the parser's output (Prints mockup of Graphene files).
    - `graphene -allPrograms` : Runs most recent compilation of source code against all Graphene files in the programs directory.

Scripts for compiled Graphene source code the day of finishing a various stage of the compiler. *Unstable*.

- `graphenes path_to_file/file_name` : Prints the tokens and their types 
- `graphenef path_to_file/file_name` : Validates Graphene files
- `graphenep path_to_file/file_name` : Prints mockup of Graphene files

## To-Do

> `State desgin pattern` : Revisit the scanner to properly implement the [state design pattern](https://sourcemaking.com/design_patterns/state) pattern.

> `Parser error handling` : Revist the parser to properly implement errors that will need to be caught at this stage.

> `Next stage for parsing` : Modify table-driven parser to produce abstract syntax tree.

> `Downsizing` : I would like to go back and visit the way nodes are implemented to lessen the number of class files. This can also be said for the way that states are implemented in the scanner.

> `Visitor Pattern` : The object used to print out the node stack could use a bit of a touch up as well, it works fine, but does not follow the pattern I initally set out to use.

> `Updating the analyzer` : With all of the changes to the parser, nodes, and node printer, I need to make sure the Analyzer is maintained and still well enough to handle all parsing errors.

`Semantic Analysis` : Now with sytax out of the way I can finally begin working on the semantics.


## Bug Fixes / Updates

- `x==y` : Fixed an issue with the scanner in which an error was thrown any time a valid identifer `x` was next to the operator `==`.

- `fn main() -> boolean print()` : Because functions must be of a return type (In this case `boolean`.) a check for the parser was added to the bodies of functions in which a print expression can not be the only expression of a function body.

- `>, ?, #` : The scanner had issues with certain charaters unrecognized by the language and will now properly handle these chracters, as well as throw correct errors.

- `missing non-terminal rules` : A function of the parser handling rules of the [parse table](https://github.com/Frost0522/Graphene/tree/main/documents) was not checking for tokens of type 'equality' and 'less than' for non-terminal rules, 'simple expression tail' and 'term tail', resulting in errors being thrown.

- `issues with identifiers` : When naming an identifier, if you were to lead, or trail behind, with characters recognized as keywords, an early syntax error would be thrown. As a consequence, an extra check has been added to State_1.

- `updated parsing errors` : Previously, when specifying the type on a parameter, if the type was either misspelled or forgotten, an incorrect error would be thrown stating the function return type is undefined.

- `current scanner output` : I accidentally forgot to make sure that the current scanners output functioned the same as it previously had. Running 'graphenes' (previous) and 'graphene -s' (current) now yield the same output.

- `improper warnings` : A warning was being given for the main function not being used in a program. Of the functions that should be warned, main is not one of them, since it is the entry point of the program and must exist unless no other functions are given.