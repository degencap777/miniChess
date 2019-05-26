package com.minichess.main;

/* MiniChess v0.5
 * 	Minimax Chess AI implemented in Java.
 * 	Created by Ryan Danver 5/17/19. ~*~
 */

import java.util.Scanner;
import com.github.bhlangonijr.chesslib.*;
import com.github.bhlangonijr.chesslib.move.*;

public class MiniChess {
	static Board board = new Board();
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		int depth;
		
		// For UNIX machines:
		System.out.println("\033[H\033[2J");
		System.out.flush();
		
		System.out.print("MiniChess AI\n"
				+ "Created by Ryan Danver 2019\n"
				+ "Type \"!\" to view the board.\n");
		
		// TODO: Implement the ability to change the computer's side/color
		// boolean color = true;
		/*System.out.println("\nBlack (false) or White (true): ");
		color = sc.nextBoolean();*/
		
		System.out.print("\nEnter search depth: ");
		depth = sc.nextInt();
		if(!(depth > 1)) { 
			System.err.println("Depth must be larger than 1.");
			return;
		}
		sc = new Scanner(System.in);
		
		/* Interactive Interpreter */
		for(;;) {
			checkBoard();
			System.out.print(">> ");
			try { 
				Evaluator.eval(sc.nextLine(),depth);
			} catch (MoveGeneratorException e) {
				e.printStackTrace(); 
			}
		}
	}
	/* Print the board to the console. */
	// TODO: Currently, the board is printed inverted. Fix this.
	public static void printBoard() {
		Piece[] pieces = board.boardToArray();
		for(int i = pieces.length - 2; i >= 0; i--) {
			System.out.print(" : ");
			switch(pieces[i]) {
			default: System.out.print(" "); break;
			case WHITE_PAWN: System.out.print("P"); break;
			case WHITE_KNIGHT: System.out.print("N"); break;
			case WHITE_BISHOP: System.out.print("B"); break;
			case WHITE_ROOK: System.out.print("R"); break;
			case WHITE_QUEEN: System.out.print("Q"); break;
			case WHITE_KING: System.out.print("K"); break;
			case BLACK_PAWN: System.out.print("p"); break;
			case BLACK_KNIGHT: System.out.print("n"); break;
			case BLACK_BISHOP: System.out.print("b"); break;
			case BLACK_ROOK: System.out.print("r"); break;
			case BLACK_QUEEN: System.out.print("q"); break;
			case BLACK_KING: System.out.print("k"); break;
			}
			if(i % 8 == 0) System.out.print(" :\n");
		}
		System.out.print("\n");
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
