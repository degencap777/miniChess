package com.minichess.main;

import java.util.Scanner;
import java.io.*;
import java.io.File;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.*;

/* MiniChess v2.6.5
 * 	MiniChess is a chess AI program that uses the minimax algorithm with alpha-beta pruning.
 *  You play as white, and MiniChess is black. Make your move by entering the two squares (e.g `>>f2f4`).
 *  Once entered, MiniChess will return it's decided move, in addition to the time (in milliseconds) it took,
 *  the amount of positions evaluated, and pruned.
 *  
 * 	Go to my website: visual-mov.com
 * 	Copyright (C) 2019 Ryan Danver ~*~
 */

public class MiniChess {
	static Board board;
	static int move, depth;
	static Scanner sc;

	public static void main(String[] args) throws Exception {
		board = new Board();
		sc = new Scanner(System.in);
		move = 1;
		
		System.out.print("\033[H\033[2J");
		System.out.print("MiniChess AI v2.6\n" + "Copyright (C) 2019 Ryan Danver\n" + "Type \"/cmds\" to view all commands.\n");

		/* Main loop */
		String input = "";
		for (;;) {
			checkBoard();
			System.out.print("\u001b[0m");
			System.out.print(">> \u001b[31;1m");

			try {
				input = sc.nextLine();
			} catch (Exception e) {
				System.exit(1);
			}
			try {
				Evaluator.eval(input, depth);
			} catch (MoveGeneratorException e) {
				System.out.println("Error generating moves.");
			} 
			catch(Exception e) {
				System.out.println("Error, plase try again.");
			}
		}
	}

	/* Print the board to the console. */
	public static void printBoard() {
		System.out.print("\n");
		Piece[] pieces = board.boardToArray();
		for (int i = 7; i >= 0; i--) {
			System.out.print("\u001b[0m");
			System.out.print(i + 1);
			System.out.print("\u001b[32;1m");
			for (int j = 0; j < 8; j++) {
				int loc = j + i * 8;
				boolean side = (pieces[loc].getPieceSide() == Side.WHITE) ? true : false;
				System.out.print(" : ");
				if (pieces[loc] == Piece.NONE) {
					System.out.print(" ");
					continue;
				} else {
					switch (pieces[loc].getPieceType()) {
					default: break;
					case PAWN: System.out.print((side) ? "P" : "p"); break;
					case KNIGHT: System.out.print((side) ? "N" : "n"); break;
					case BISHOP: System.out.print((side) ? "B" : "b"); break;
					case ROOK: System.out.print((side) ? "R" : "r"); break;
					case QUEEN: System.out.print((side) ? "Q" : "q"); break;
					case KING: System.out.print((side) ? "K" : "k"); break;
					}
				}
			}
			System.out.print(" :\n");
		}
		System.out.print("\u001b[0m    A   B   C   D   E   F   G   H\n\n");
	}

	/* Checks if the game has ended. */
	public static void checkBoard() {
		if (board.isDraw())
			endGame("draw");
		else if (board.isMated())
			endGame("checkmate");
		else if (board.isStaleMate())
			endGame("stalemate");
	}

	public static void endGame(String cause) {
		System.out.println("The game has ended. It's a " + cause + ".");
		System.exit(0);
	}

	/* Saves the current state of the board to a file. */
	public static void saveBoard(String name) throws IOException {
		File file = new File(name);
		if (file.createNewFile())
			System.out.println("Board saved.");
		else
			System.out.println("Couldn't save board.");

		FileWriter writer = new FileWriter(file);
		writer.write(board.getFen() + "\n" + depth + "\n" + move);
		writer.close();
	}

	/* Reads board state from file */
	public static void loadBoard(String path) throws FileNotFoundException {
		move = 1;
		Scanner sc = new Scanner(new File(path));
		int index = 0;
		board.clear();
		while (sc.hasNextLine()) {
			if(index == 0) board.loadFromFen(sc.nextLine());
			else if(index == 1) depth = sc.nextInt();
			else if(index == 2) move = sc.nextInt();
			index++;
		}
		sc.close();
	}
	
	/* Resets the game. */
	static void resetGame() {
		System.out.print("\033[2JGame reset.\n");
		board = new Board();
		move = 1;
	}
}
