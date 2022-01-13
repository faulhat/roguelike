maze-compile:
	javac Maze.java

maze-debug: maze-compile
	java Maze 8 9 debug

maze: maze-compile
	java Maze
