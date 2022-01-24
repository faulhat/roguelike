maze-build:
	javac Maze.java

maze: maze-build
	java Maze

build:
	javac *.java

run-main: build
	java Game