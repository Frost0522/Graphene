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

> If you want to customize the folder structure, open `.vscode/settings.json` and update the related settings there.

> If you would like to see more on the full language specification as well, [this](https://www.cs.uni.edu/~wallingf/teaching/cs4550/compiler/specification.html) will provide further details.

## General

There are going to be multiple uploads as I work my way through the various stages of the compiler construction process. First will be the scanner (completed, but not final!), then the parser, semantic checker, and finally the code generation. More on these later. This project was part of my final undergraduate research that was done in a group. I would like to now look back on this experience and work on the parts of the compiler that, orginally, I never got to fully understand.

> If you would like to run the compiler in its current state, just the scanner (produces tokens of the language), simply run graphenes in source followed by a Graphene program of your choosing. 

## To-Do

- `State desgin pattern` : Revisit the scanner to properly implement [this](https://sourcemaking.com/design_patterns/state) pattern.