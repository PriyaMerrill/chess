package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class QueenMoves {
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition){
        int [][] directions = {{-1, 1}, {0, 1}, {1, 1}, {1, 0}, {1, -1}, {0, -1}, {-1, -1}, {-1, 0}};
        return MoveCalculator.generateMoves(board, startPosition, directions, false);
    }
}
