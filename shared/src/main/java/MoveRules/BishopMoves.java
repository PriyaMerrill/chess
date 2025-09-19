package MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class BishopMoves implements MoveCalculator {
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition) {
        int[][] directions = {{-1,1}, {1,1}, {1,-1},{-1,-1}};

        return MoveCalculator.generateMoves(board, startPosition, directions, false);
    }
}
