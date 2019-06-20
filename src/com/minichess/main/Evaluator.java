package com.minichess.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Rank;
import com.github.bhlangonijr.chesslib.Side;
import com.github.bhlangonijr.chesslib.Square;
import com.github.bhlangonijr.chesslib.move.*;

/* The Evaluator Class.
 * 	Evaluates the current position, and decides
 * 	the computer's move.
 */

public class Evaluator {
	static int posNum, pruneNum;
	static String token;
	static boolean initDepth = false;
	static boolean printOnMove = false;
	static int pInfinity,nInfinity;

	public static void eval(String in, int depth) throws MoveGeneratorException, Exception  {
		System.out.print("\u001b[0m");
		Move givenMove;
		MoveList moves = MoveGenerator.generateLegalMoves(MiniChess.board);
		boolean isLegal = false;
		posNum = 0;
		pruneNum = 0;
		pInfinity = (int) Double.POSITIVE_INFINITY;
		nInfinity = (int) Double.NEGATIVE_INFINITY;
		
		
		token = "";
		char[] input = in.toCharArray();
		boolean nextSquare = false;
		boolean isCommand = false;
		Square[] squares = new Square[2];
		
		/* Parsing the input */
		for (int i = 0; i < input.length; i++) {
			token += input[i];
			if (input[i] == '/') {
				isCommand = true;
				token = "";
			}
			if (isCommand) {
				String param = getParam(input, i);
				switch (token) {
				default: break;
				case "printboard": MiniChess.printBoard(); break;
				case "exit": System.exit(1); break;
				case "resetgame": MiniChess.resetGame(); break;
				case "alwaysprint": printOnMove = (Boolean.parseBoolean(param)) ? true : false; break;
				case "setdepth":
					try {
						initDepth = true;
						MiniChess.depth = Integer.parseInt(param);
					} catch(NumberFormatException e) {
						System.out.println("Please enter an integer.");
						initDepth = false;
					}
					break;
				case "save":
					try { MiniChess.saveBoard(param); }
					catch (IOException e) { System.out.println("Could not create file."); }
					break;
				case "load":
					try {MiniChess.loadBoard(param);} 
					catch (FileNotFoundException e) { System.out.println("Could not find file."); }
					break;
				case "fen":
					if(initDepth) {
						MiniChess.resetGame();
						MiniChess.board.loadFromFen(param);
						if(MiniChess.board.getSideToMove() == Side.BLACK) {
							makeComputerMove(moves,depth);
							return;
						}
					}
					break;
				case "version":
					System.out.println("MiniChess AI v2.6.5 Created 5/17/19\n\n" + "Credits:\n"
							+ " | MiniChess v2.6.5 - Copyright (C) 2019 Ryan Danver\n"
							+ " | chesslib - Copyright 2017 Ben-Hur Carlos Vieira Langoni Junior");
					break;
				case "cmds":
					System.out.println(
							"List of all commands:\n" 
									+ " | /save [path] - Saves the current board state to a file.\n"
									+ " | /load [path] - Loads board state from file.\n"
									+ " | /fen [string] - Loads board state from a FEN string.\n"
									+ " | /setdepth [depth] - Sets the search depth for the minimax tree.\n"
									+ " | /printboard - Prints a representation of the current board.\n"
									+ " | /resetgame - Clears the console, and resets the board and move counter.\n"
									+ " | /alwaysprint [boolean] - Sets whether to print the board on every move.\n"
									+ " | /exit - Exits the program.\n" 
									+ " | /cmds - Prints all commands.\n"
									+ " | /version - Prints version and credits.");
					break;
				}
			} else {
				if (Character.isAlphabetic(input[i])) {
					squares[(nextSquare) ? 1 : 0] = Square.valueOf(formatMove(input, i));
					nextSquare = !nextSquare;
				}
			}
		}
		if (isCommand) return;
		
		if(squares[1].getRank() == Rank.RANK_8 && MiniChess.board.getPiece(squares[0]) == Piece.WHITE_PAWN) 
			givenMove = new Move(squares[0], squares[1],Piece.WHITE_QUEEN);
		else
			givenMove = new Move(squares[0], squares[1]);
		
		/* Checks for depth. */
		if(!initDepth) {
			System.out.println("Please set initial depth.");
			return;
		} else if(depth <= 0) {
			System.out.println("Depth must be larger than 0.");
			System.exit(1);
		}
		
		/* Determining whether the player's move was legal. If so, update the board. */
		for (Move m : moves)
			if (m.equals(givenMove))
				isLegal = true;
		if (!isLegal) {
			System.out.println("Illegal Move! Please enter again.");
			return;
		} else
			MiniChess.board.doMove(givenMove);
		
		makeComputerMove(moves, depth);
	}

	/* Deciding, and applying the computer's move. */
	public static void makeComputerMove(MoveList moves, int depth) throws MoveGeneratorException {
		moves = MoveGenerator.generateLegalMoves(MiniChess.board);
		long curTime = System.currentTimeMillis();
		Move cMove = rootMiniMax(true, depth);
		long elapsedTime = System.currentTimeMillis() - curTime;
		
		if(cMove != null) MiniChess.board.doMove(cMove);
		else return;
		
		if(printOnMove) MiniChess.printBoard();
		System.out.println("\nmove: " + MiniChess.move);
		System.out.print((MiniChess.board.isKingAttacked()) ? " | \u001b[31;1mYou're in check.\n\u001b[0m" : "");
		System.out.println(" | Elapsed time: " + elapsedTime + "ms.\n" +
						   " | Positions evaled: " + posNum + "\n" +
						   " | Positions pruned: " + pruneNum);
		System.out.println(" | (\u001b[36m" + cMove + "\u001b[0m)\n");
		
		MiniChess.move++;
	}

	/* Minimax algorithm with alpha-beta pruning. 
	 * 	For more information, go to https://en.wikipedia.org/wiki/Minimax.
	 */

	/* For the root node. */
	public static Move rootMiniMax(boolean maximizing, int depth) throws MoveGeneratorException {
		Move bestMove = null;
		MoveList newMoves = MoveGenerator.generateLegalMoves(MiniChess.board);
		int bestValue = (int) Double.NEGATIVE_INFINITY;
		for (int i = 0; i < newMoves.size(); i++) {
			MiniChess.board.doMove(newMoves.get(i));
			int miniMaxValue = miniMax(!maximizing, depth - 1, nInfinity, pInfinity);
			MiniChess.board.undoMove();
			if (miniMaxValue >= bestValue) {
				bestValue = miniMaxValue;
				bestMove = newMoves.get(i);
			}
		}
		return bestMove;
	}

	/* For all child nodes. */
	public static int miniMax(boolean maximizing, int depth, int alpha, int beta) throws MoveGeneratorException {
		posNum++;
		if (depth == 0)
			return -PieceEvaluator.boardValue(MiniChess.board);
		MoveList newMoves = MoveGenerator.generateLegalMoves(MiniChess.board);
		
		if (maximizing) {
			int bestValue = nInfinity;
			for (int i = 0; i < newMoves.size(); i++) {
				MiniChess.board.doMove(newMoves.get(i));
				int value = miniMax(!maximizing, depth - 1, alpha, beta);
				bestValue = Math.max(bestValue, value);
				MiniChess.board.undoMove();
				alpha = Math.max(alpha, value);
				if (beta <= alpha) {
					pruneNum++;
					break;
				}
			}
			return bestValue;
		} else {
			int bestValue = pInfinity;
			for (int i = 0; i < newMoves.size(); i++) {
				MiniChess.board.doMove(newMoves.get(i));
				int value = miniMax(!maximizing, depth - 1, alpha, beta);
				bestValue = Math.min(bestValue, value);
				MiniChess.board.undoMove();
				beta = Math.min(beta, value);
				if (beta <= alpha) {
					pruneNum++;
					break;
				}
			}
			return bestValue;
		}
	}

	/* Functions for parser */
	public static String getParam(char[] chars, int index) {
		String command = "";
		boolean parse = false;
		for (int i = index + 1; i < chars.length; i++) {
			if (chars[i] == '\n') break;
			if(chars[i] != ' ') parse = true;
			if(parse) command += chars[i];
		}
		return command;
	}

	public static String formatMove(char[] chars, int index) {
		return Character.toString(Character.toUpperCase(chars[index]))
				+ Character.toString(Character.toUpperCase(chars[index + 1]));
	}
}
