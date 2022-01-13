# roguelike
A roguelike game (somewhat)

Maze generation program:

To build:

`make maze-compile`

To build and run in debug mode:

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
