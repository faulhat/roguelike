# roguelike
A roguelike game (somewhat)

Maze generation program:

This program generates simple mazes using the Recursive Division Method. See here for details: https://en.wikipedia.org/wiki/Maze_generation_algorithm#Recursive_division_method. It will be used to procedurally generate levels.

To build:

`make maze-compile`

To build and run in debug mode (output info, use a constant seed.):

`make maze-debug`

To build and run normally:

`make maze`

To run (after compilation):

`java Maze`

Width and height default to 10 and 12 respectively.

To specify width and height:

```
java Maze [width] [height]
# For example:
java Maze 8 9
```

To run in debug mode (must specify dimensions):

```
java Maze [width] [height] debug
# For example:
java Maze 8 9 debug
```


To run in debug mode, but still use a random seed:

```
java Maze [width] [height] debug-rand
# For example:
java Maze 8 9 debug-rand
```

Game demo:

To build the code:

`make build-all`

To build and run:

`make run-main`

To run after building:

`java Game`

For now you can just travel from room-to-room in an empty procedurally generated level. More stuff will be added soon.
