# AI-CSCI561
Code written for the CSCI 561 Foundations of Artificial Intelligence class

# HW1 - Search
The problem has a nxn zoo, with lizards that need to be placed so that they do not attack each other (like the n queens problem). However, the modification places k trees, which makes 2 lizards on the same row/column/diagonal safe if there is a tree between them.

Algorithms implemented: DFS, BFS, SA, N-Queens when k = 0

Language: Java

# HW2 - Minimax Game Playing
This is a game (called fruitrage) where each player aims to collect maximum fruits by choosing connected areas of the same fruit (number). The calibrate.py runs the game on a sample input to check the amount of time taken to play one move. Based on the result written in calibrate.txt, the cutoff depth of the alpha-beta pruning is determined dynamically.

Algorithm implemented: Alpha-beta pruning with node ordering

Language: Python

# HW3 - Logic
Implemented a First Order Logic Resolution engine for sentences in CNF form (only NOT and OR operators). This uses unification of sentences, including variable to variable unification.

Algorithm implemeted: Unification, Resolution

Language: Python
