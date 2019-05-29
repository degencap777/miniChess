package com.minichess.main;

/* MiniChess v1.0
 * 	Minimax Chess AI with alpha-beta pruning implemented in Java.
 * 	Created by Ryan Danver 5/17/19. ~*~
 */

import java.util.Scanner;
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
		System.out.print("MiniChess AI\n"
				+ "Created by Ryan Danver 2019\n"
				+ "Type \"!\" to view the board.\n");
		
		// TODO: Implement the ability to change the computer's side/color
		// boolean color = true;
		// System.out.print("\nBlack (false) or White (true): ");
		// side = sc.nextBoolean();
		
		System.out.print("\nEnter search depth: ");
		depth = sc.nextInt();
		if(!(depth > 1)) { 
			System.out.println("Depth must be larger than 1.");
			return;
		}
		sc = new Scanner(System.in);
		
		/* Interactive Interpreter */
		for(;;) {
			System.out.print("\u001b[0m");
			checkBoard();
			System.out.println("\nmove: " + move);
			System.out.print(">> \u001b[31;1m");
			try { 
				Evaluator.eval(sc.nextLine(), depth);
			} catch (MoveGeneratorException e) {
				e.printStackTrace();
			}
		}
	}
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
	public static void checkBoard() {
		if(board.isDraw()) endGame("draw");
		else if(board.isMated()) endGame("checkmate");
		else if(board.isStaleMate()) endGame("stalemate");
	}
	public static void endGame(String cause) {
		System.out.println("The game has ended. It's a " + cause + ".");
		System.exit(0);
	}
}
