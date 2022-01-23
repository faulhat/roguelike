maze-build:
	javac Maze.java

maze: maze-build
	java Maze

build-all:
	javac *.java

run-main: build-all
	java Game