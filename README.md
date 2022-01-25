# De Re Publica
A game somewhere between a roguelike and an RPG that takes place in the Late Republic. Named for a political treatise by Cicero.

How to get the working version:

```
git clone https://github.com/tafaulhaber590/roguelike/
cd roguelike
```

As of now, the game has three levels with a boss at the end of each level. The levels are procedurally generated. They are very empty, unfortunately. You can randomly encounter different enemies in each level and buy stuff from merchants. You can also save the game.

To build and run the main program:

```
javac *.java
java Game
```

To get maps of each level printed to the command line when you start a new game:

```
java Game debug
```


Maze generation program:

This repository contains a program that generates simple mazes using the Recursive Division Method. See here for details: https://en.wikipedia.org/wiki/Maze_generation_algorithm#Recursive_division_method. This program is used to procedurally generate levels.

To build:

```
javac Maze.java
```

To run with default parameters:

```
java Maze
```

Width and height default to 10 and 12 respectively.

To specify width and height:

```
java Maze [width] [height]
```

For example:

```
java Maze 8 9
```

You can also run the program in debug mode, which will print each step of the algorithm to the command line and use a constant seed.

To run in debug mode (must specify dimensions):

```
java Maze [width] [height] debug
```

To run in debug mode, but still use a random seed:

```
java Maze [width] [height] debug-rand
```

Credits:

Ricardo Camba

Thomas Faulhaber

Athenase Nishemezwe

Lukas Probst

Tobias Waldron
