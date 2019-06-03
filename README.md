# MiniChess v.2.0
![Console Output](image.jpg)

MiniChess is a simplistic Chess AI program that uses the minimax algorithm with alpha-beta pruning. You play as white, and MiniChess is black. Make your move by entering the two squares (e.g `>>f2f4`) Once entered, MiniChess will return it's decided move, in addition to the amount of positions evaluated, and pruned.

Note: While it might seem logical to add up the evaluated and pruned positions to get the total number of possible positions, this isn't true. Instead, it refers to the amount of positions where parts of the tree where cut off, meaning the actual amount of possible positions can be much greater. 

## Commands
MiniChess has several built in commands. Here's a list of them all, and their different functions:

`/save [name]` - Saves the current board state to a file.

`/load [name]` - Loads board state from file.

`/setDepth [depth]` - Sets the search depth for the minimax tree.

`/printBoard` - Prints a representation of the current board.

`/exit` - Exits the program.

`/cmds` - Prints all commands.

`/version` - Prints version and credits.
