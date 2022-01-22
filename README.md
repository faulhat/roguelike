# roguelike
A roguelike game (somewhat)

How to get the demo version:

```
git clone https://github.com/tafaulhaber590/roguelike/
cd roguelike
```

Right now, the demo version is very simple. You can randomly encounter basic enemies while wandering around an empty level. You can't save yet. Dying just returns you to the main menu.

How to run the demo:

```
# Compile...
make build-all

# ...and run
java Game
```

Maze generation program:

This repository contains a program that generates simple mazes using the Recursive Division Method. See here for details: https://en.wikipedia.org/wiki/Maze_generation_algorithm#Recursive_division_method. This program is used to procedurally generate levels.

To build:

```
make maze-compile
```

To run with default parameters:

```
java Maze
```

Width and height default to 10 and 12 respectively.

To specify width and height:

```
java Maze [width] [height]
# For example:
java Maze 8 9
```

You can also run the program in debug mode, which will print each step of the algorithm to the command line and use a constant seed.

To run in debug mode (must specify dimensions):

```
java Maze [width] [height] debug
```

For example:

```
java Maze 8 9 debug
```

To run in debug mode, but still use a random seed:

```
java Maze [width] [height] debug-rand
```

For example:

```
java Maze 8 9 debug-rand
```