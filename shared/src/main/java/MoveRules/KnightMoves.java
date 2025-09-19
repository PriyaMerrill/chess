package MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class KnightMoves implements MoveCalculator {
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition){
        int [][] directions = {{-2, 1}, {-2, -1}, {2, 1}, {2, -1}, {1, 2}, {-1, 2}, {1, -2}, {-1, -2}};
        return MoveCalculator.generateMoves(board, startPosition, directions, true);
    }
}