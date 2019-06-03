package com.minichess.main;

/* MiniChess v2.0
 * 	Minimax Chess AI with alpha-beta pruning implemented in Java.
 * 	Go to my website: visual-mov.com
 * 
 * 	Copyright (C) 2019 Ryan Danver ~*~
 */

import java.util.Scanner;
import java.io.*;
import java.io.File;

import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.*;

public class MiniChess {
	static Board board;
	static int move, depth;
	public static void main(String[] args) {
		board = new Board();
		Scanner sc = new Scanner(System.in);
		move = 1;
		
		System.out.print("\033[H\033[2J");
		System.out.flush();
		System.out.print("MiniChess AI v2.0\n"
				+ "Copyright (C) 2019 Ryan Danver\n"
				+ "Type \"/cmds\" to view all commands.\n");
		
		// TODO: Implement the ability to change the computer's side/color
		// boolean side = true;
		// System.out.print("\nBlack (false) or White (true): ");
		// side = sc.nextBoolean();
		
		System.out.print("\nEnter initial search depth: ");
		depth = sc.nextInt();
		sc = new Scanner(System.in);
		/* Interactive Interpreter */
		for(;;) {
			if(depth <= 1) { 
				System.out.println("Depth must be larger than 1.");
				System.exit(1);
			}
			checkBoard();
			System.out.print("\u001b[0m");
			System.out.println("\nmove: " + move);
			System.out.print(">> \u001b[31;1m");
			try { 
				Evaluator.eval(sc.nextLine(), depth);
			} catch (MoveGeneratorException e) {
				System.out.println("Error generating moves.");
			}
		}
	}
	// TODO: The board is mirrored. Fix this.
	/* Print the board to the console. */
	public static void printBoard() {
		System.out.println("\u001b[32;1m");
		Piece[] pieces = board.boardToArray();
		for(int i = 0; i < pieces.length; i++) {
			boolean side;
			if(pieces[i].getPieceSide() == Side.WHITE) side = true;
			else side = false;
			
			if(i % 8 == 0 && i != 0) 
				System.out.print(" :\n");
			if(i != pieces.length - 1) 
				System.out.print(" : ");
			if(pieces[i].getPieceType() == null) 
				System.out.print(" ");
			else {
				switch(pieces[i].getPieceType()) {
				default: break;
				case PAWN: if(side) System.out.print("P"); else System.out.print("p"); break;
				case KNIGHT: if(side) System.out.print("N"); else System.out.print("n"); break;
				case BISHOP: if(side) System.out.print("B"); else System.out.print("b"); break;
				case ROOK: if(side) System.out.print("R"); else System.out.print("r"); break;
				case QUEEN: if(side) System.out.print("Q"); else System.out.print("q"); break;
				case KING: if(side) System.out.print("K"); else System.out.print("k"); break;
				}
			}
		}
		System.out.print("\u001b[0m");
	}
	/* Checks if the game has ended. */
	public static void checkBoard() {
		if(board.isDraw()) endGame("draw");
		else if(board.isMated()) endGame("checkmate");
		else if(board.isStaleMate()) endGame("stalemate");
	}
	public static void endGame(String cause) {
		System.out.println("The game has ended. It's a " + cause + ".");
		System.exit(0);
	}
	
	/* Saves the current state of the board to a file. */
	public static void saveBoard(String name) throws IOException {
		File file = new File(name);
		if(file.createNewFile()) System.out.println("Board saved.");
		else System.out.println("Couldn't save board.");
		
		FileWriter writer = new FileWriter(file);
		Piece[] pieces = board.boardToArray();
		for(int i = 0; i < pieces.length; i++)
			if(i != pieces.length - 1)
				writer.write(pieces[i].toString() + "\r\n");
		writer.close();
	}
	
	/* Reads board state from file */
	public static void loadBoard(String path) throws FileNotFoundException {
		move = 1;
		Scanner sc = new Scanner(new File(path));
		int index = 0;
		board.clear();
		while(sc.hasNextLine()) {
			Piece piece = Piece.valueOf(sc.nextLine());
			if(piece != Piece.NONE)
				board.setPiece(piece, Square.squareAt(index));
			index++;
		}
		sc.close();
	}
}
