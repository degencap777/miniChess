package com.minichess.main;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.*;

/* The Evaluator Class.
 * 	Evaluate the given position, and decides
 * 	the computer's move.
 */
public class Evaluator {
	public static void eval(String in, int depth) throws MoveGeneratorException {
		Move givenMove;
		MoveList moves = MoveGenerator.generateLegalMoves(MiniChess.board);
		boolean isLegal = false;
		
		/* Parsing Input */
		// TODO: Add error handling, and make parsing routine more robust.
		
		char[] input = in.toCharArray();
		boolean nextSquare = false;
		Square[] squares = new Square[2];
		for (int i = 0; i < input.length; i++) {
			if(input[i] == '!') {
				MiniChess.printBoard();
				return;
			}
			if(i != input.length - 1) {
				if (input[i] == '-')
					nextSquare = true;
				if (Character.isAlphabetic(input[i])) {
					if (!nextSquare) squares[0] = Square.valueOf(formatMove(input, i));
					else squares[1] = Square.valueOf(formatMove(input, i));
				}
			}
		}
		givenMove = new Move(squares[0], squares[1]);
		
		/* Determining whether the player's move was legal. If so, update the board. */
		for (Move m : moves)
			if (m.equals(givenMove))
				isLegal = true;
		if (!isLegal) {
			System.err.println("Invalid Move! Please enter again.");
			return;
		} else
			MiniChess.board.doMove(givenMove);
		makeComputerMove(moves,depth);
	}
	
	/* Deciding, and applying the computer's move. */
	public static void makeComputerMove(MoveList moves, int depth) throws MoveGeneratorException {
		moves = MoveGenerator.generateLegalMoves(MiniChess.board);
		Move cMove = rootMiniMax(true,depth);
		MiniChess.board.doMove(cMove);
		System.out.println(" | " + cMove);
	}
	
	/* Minimax algorithm.
	 * 	For more information, go to https://en.wikipedia.org/wiki/Minimax.
	 */
	
	/* For the root node. */
	private static Move rootMiniMax(boolean maximizing, int depth) throws MoveGeneratorException {
		Move bestMove = null;
		MoveList newMoves = MoveGenerator.generateLegalMoves(MiniChess.board);
		int bestValue = -99999;
		for(int i = 0; i < newMoves.size(); i++) {
			MiniChess.board.doMove(newMoves.get(i));
			int miniMaxValue = miniMax(!maximizing, depth - 1);
			MiniChess.board.undoMove();
			if(miniMaxValue >= bestValue) {
				bestValue = miniMaxValue;
				bestMove = newMoves.get(i);
			}
		}
		return bestMove;
	}
	
	/* For all child nodes. */
	private static int miniMax(boolean maximizing, int depth) throws MoveGeneratorException {
		if(depth == 0) return -getBoardValue(MiniChess.board);
		MoveList newMoves = MoveGenerator.generateLegalMoves(MiniChess.board);
		if(maximizing) {
			int bestValue = -999999;
			for(int i = 0; i < newMoves.size(); i++) {
				MiniChess.board.doMove(newMoves.get(i));
				bestValue = Math.max(bestValue,miniMax(!maximizing, depth - 1));
				MiniChess.board.undoMove();
			}
			
			return bestValue;
		} else {
			int bestValue = 999999;
			for(int i = 0; i < newMoves.size(); i++) {
				MiniChess.board.doMove(newMoves.get(i));
				bestValue = Math.min(bestValue,miniMax(!maximizing, depth - 1));	
				MiniChess.board.undoMove();
			}
			return bestValue;
		}
	}

	/* Get the total board value */
	private static int getBoardValue(Board b) {
		int value = 0;
		for (Piece p : b.boardToArray()) {
			value += getPieceValue(p);
		}
		return value;
	}

	/* Get the value for one piece */
	private static int getPieceValue(Piece p) {
		switch (p) {
		case WHITE_PAWN: return 100;
		case BLACK_PAWN: return -100;
		case WHITE_KNIGHT: return 300;
		case BLACK_KNIGHT: return -300;
		case WHITE_BISHOP: return 300;
		case BLACK_BISHOP: return -300;
		case WHITE_ROOK: return 500;
		case BLACK_ROOK: return -500;
		case WHITE_QUEEN: return 1000;
		case BLACK_QUEEN: return -1000;
		case WHITE_KING: return 1000000;
		case BLACK_KING: return -1000000;
		default: return 0;
		}
	}
	
	private static String formatMove(char[] chars, int index) {
		return Character.toString(Character.toUpperCase(chars[index]))
				+ Character.toString(Character.toUpperCase(chars[index + 1]));
	}
}
