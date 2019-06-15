package com.minichess.main;

import com.github.bhlangonijr.chesslib.Board;
import com.github.bhlangonijr.chesslib.Piece;
import com.github.bhlangonijr.chesslib.Side;

public class PieceEvaluator {
	
	/* Piece-Square Tables*/
	public static int[][] pTable = {
			{20, 20, 20, 20, 20, 20, 20, 20},
			{40, 40, 40, 40, 40, 40, 40, 40},
			{10, 10, 20, 30, 30, 20, 10, 10},
			{ 5,  5, 10, 25, 25, 10,  5,  5},
			{ 0,  0,  0, 20, 20,  0,  0,  0},
			{ 5, -5,-10,  0,  0,-10, -5,  5},
			{ 5, 10, 10,-20,-20, 10, 10,  5},
			{ 0,  0,  0,  0,  0,  0,  0,  0}
	};
	
	public static int[][] endpTable = {
			{60, 60, 60, 65, 65, 60, 60, 60},
			{40, 40, 40, 50, 50, 40, 40, 40},
			{10, 10, 20, 30, 30, 20, 10, 10},
			{ 5,  5, 10, 25, 25, 10,  5,  5},
			{ 0,  0,  0, 20, 20,  0,  0,  0},
			{ 5, -5,-10,  0,  0,-10, -5,  5},
			{ 5, 10, 10,-20,-20, 10, 10,  5},
			{ 0,  0,  0,  0,  0,  0,  0,  0}
	};
	
	/* The knight's table doesn't need to be mirrored, since it's symmetrical. */
	public static int[][] nTable = {
			{-50,-40,-30,-30,-30,-30,-40,-50},
			{-40,-20,  0,  0,  0,  0,-20,-40},
			{-40,  0, 10, 15, 15, 10,  0,-40},
			{-30,  5, 15, 25, 25, 15,  5,-30},
			{-30,  0, 15, 25, 25, 15,  0,-30},
			{-40,  5, 10, 15, 15, 10,  5,-40},
			{-40,-20,  0,  5,  5,  0,-20,-40},
			{-50,-40,-30,-30,-30,-30,-40,-50}
	};
	public static int[][] bTable = {
			{-20,-10,-10,-10,-10,-10,-10,-20},
			{-20,  0,  0,  0,  0,  0,  0,-20},
			{-10,  0,  5, 10, 10,  5,  0,-10},
			{-10,  5,  5, 10, 10,  5,  5,-10},
			{-10,  0, 10, 10, 10, 10,  0,-10},
			{-10, 10, 10, 10, 10, 10, 10,-10},
			{-20,  5,  0,  0,  0,  0,  5,-20},
			{-20,-10,-10,-10,-10,-10,-10,-20}
	};
	public static int[][] rTable = {
			{ 0,  0,  0,  0,  0,  0,  0,  0},
			{ 5, 10, 10, 10, 10, 10, 10,  5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{-5,  0,  0,  0,  0,  0,  0, -5},
			{ 0,  0,  0,  5,  5,  0,  0,  0}
	};
	public static int[][] qTable = {
			{-20,-10,-10, -5, -5,-10,-10,-20},
			{-10,  0,  0,  0,  0,  0,  0,-10},
			{-10,  0,  5,  5,  5,  5,  0,-10},
			{ -5,  0,  5,  5,  5,  5,  0, -5},
			{  0,  0,  5,  5,  5,  5,  0, -5},
			{-10,  5,  5,  5,  5,  5,  0,-10},
			{-10,  0,  5,  0,  0,  0,  0,-10},
			{-20,-10,-10, -5, -5,-10,-10,-20}
	};
	public static int[][] kTable = {
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-40,-40,-50,-50,-40,-40,-30},
			{-30,-30,-40,-50,-50,-40,-30,-30},
			{-20,-20,-30,-30,-30,-30,-20,-20},
			{-10,-20,-20,-20,-20,-20,-20,-10},
			{ 20, 20,  0,  0,  0,  0, 20, 20},
			{ 20, 40, 10,  0,  0, 10, 40, 20}
	};
	
	public static int[][] endkTable = {
			{-50,-40,-30,-20,-20,-30,-40,-50},
			{-30,-20,-10,  0,  0,-10,-20,-30},
			{-30,-10, 20, 30, 30, 20,-10,-30},
			{-30,-10, 30, 40, 40, 30,-10,-30},
			{-30,-10, 30, 40, 40, 30,-10,-30},
			{-30,-10, 20, 30, 30, 20,-10,-30},
			{-30,-30,  0,  0,  0,  0,-30,-30},
			{-50,-30,-30,-30,-30,-30,-30,-50}
	};

	/* Get the total board value */
	public static int boardValue(Board b) {
		int value = 0;
		Piece[] pieces = b.boardToArray();
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				value += pieceValue(y,x,pieces[x+y*8]);
			}
		}
		return value;
	}
	
	/* Get the value of one piece. 
	 * 	The integer returned factors in the static value of the piece, its location, and its side. 
	 */
	public static int pieceValue(int x, int y, Piece p) {
		boolean side = false;
		if (p.getPieceSide() == Side.WHITE)
			side = true;

		if (p.getPieceType() != null) {
			switch (p.getPieceType()) {
			default: return 0;
			case PAWN: 
				if(countTotalPieces() < 8)
					return (side) ? 200 + endpTable[x][y] : -200 + -mirrorArray(endpTable)[x][y];
				else
					return (side) ? 200 + pTable[x][y] : -200 + -mirrorArray(pTable)[x][y];
			case KNIGHT: return (side) ? 600 + nTable[x][y] : -600 + -nTable[x][y];
			case BISHOP: return (side) ? 650 + bTable[x][y] : -650 + -mirrorArray(bTable)[x][y];
			case ROOK: return (side) ? 1000 + rTable[x][y] : -1000 + -mirrorArray(rTable)[x][y];
			case QUEEN: return (side) ? 2000 + qTable[x][y] : -2000 + -mirrorArray(qTable)[x][y];
			case KING:
				if(countTotalPieces() < 8)
					return (side) ? 100000 +  endkTable[x][y] : -100000 + -mirrorArray(endkTable)[x][y];
				else {
					return (side) ? 100000 +  kTable[x][y] : -100000 + -mirrorArray(kTable)[x][y];
				}
			}
		} else return 0;
	}
	/* Returns mirrored copy of the given 2 dimensional array.
	 *	Black uses the same tables with mirrored values.
	 */
	public static int[][] mirrorArray(int[][] arr) {
		int[][] newArr = new int[8][8];
	    for(int i = 0; i < (arr.length / 2); i++) {
	    	int[] temp = arr[i];
	        newArr[i] = arr[arr.length - i - 1];
	        newArr[arr.length - i - 1] = temp;
	    }
	    return newArr;
	}
	
	/* Returns total amount of pieces on the board. 
	 * 	Used to determine whether the game has reached the endgame.
	 * 	The line between the endgame and middlegame isn't super clear,
	 * 	but I'll assume it's when both sides have 4 pieces or less.
	 */
	public static int countTotalPieces() {
		int count = 0;
		Piece[] pieces = MiniChess.board.boardToArray();
		for(int i = 0; i < pieces.length; i++)
			if(pieces[i] != Piece.NONE) count++;
		return count;
	}
}
