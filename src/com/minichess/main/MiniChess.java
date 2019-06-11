package com.minichess.main;

import java.util.Scanner;
import java.io.*;
import java.io.File;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.*;

/* MiniChess v2.5
 * 	Minimax Chess AI with alpha-beta pruning implemented in Java.
 * 	Go to my website: visual-mov.com
 * 
 * 	Copyright (C) 2019 Ryan Danver ~*~
 */

public class MiniChess {
	static Board board;
	static int move, depth;
	static Scanner sc;

	public static void main(String[] args) {
		board = new Board();
		sc = new Scanner(System.in);
		move = 1;
		
		System.out.print("\033[H\033[2J");
		System.out.flush();
		System.out.print("MiniChess AI v2.5\n" + "Copyright (C) 2019 Ryan Danver\n" + "Type \"/cmds\" to view all commands.\n");
		
		/* TODO: Exeption handling is a little sloppy. Fix this later. */
		/* Main loop */
		String input = "";
		for (;;) {
			checkBoard();
			System.out.print("\u001b[0m");
			System.out.println("\nmove: " + move);
			System.out.print(">> \u001b[31;1m");
			
			try { input = sc.nextLine(); } 
			catch(Exception e) { System.exit(1); }
			try {
				Evaluator.eval(input, depth);
			} catch (MoveGeneratorException e) {
				System.out.println("Error generating moves.");
			} catch(Exception e) {
				System.out.println("Error, plase try again.");
			}
		}
	}

	// TODO: The board is mirrored. Fix this.
	/* Print the board to the console. */
	public static void printBoard() {
		System.out.println("\u001b[32;1m");
		Piece[] pieces = board.boardToArray();
		for (int i = 0; i < pieces.length; i++) {
			boolean side = (pieces[i].getPieceSide() == Side.WHITE) ? true : false;
			
			if (i % 8 == 0 && i != 0)
				System.out.print(" :\n");
			if (i != pieces.length - 1)
				System.out.print(" : ");
			if (pieces[i].getPieceType() == null)
				System.out.print(" ");
			else {
				switch (pieces[i].getPieceType()) {
				default: break;
				case PAWN:
					System.out.print((side) ? "P" : "p");
					break;
				case KNIGHT:
					System.out.print((side) ? "N" : "n");
					break;
				case BISHOP:
					System.out.print((side) ? "B" : "b");
					break;
				case ROOK:
					System.out.print((side) ? "R" : "r");
					break;
				case QUEEN:
					System.out.print((side) ? "Q" : "q");
					break;
				case KING:
					System.out.print((side) ? "K" : "k");
					break;
				}
			}
		}
		System.out.print("\u001b[0m");
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
		Piece[] pieces = board.boardToArray();
		for (int i = 0; i < pieces.length; i++)
			if (i != pieces.length - 1)
				writer.write(pieces[i].toString() + "\r\n");
		writer.close();
	}

	/* Reads board state from file */
	public static void loadBoard(String path) throws FileNotFoundException {
		move = 1;
		Scanner sc = new Scanner(new File(path));
		int index = 0;
		board.clear();
		while (sc.hasNextLine()) {
			Piece piece = Piece.valueOf(sc.nextLine());
			if (piece != Piece.NONE)
				board.setPiece(piece, Square.squareAt(index));
			index++;
		}
		sc.close();
	}
}
