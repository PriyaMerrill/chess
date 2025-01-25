package chess.MoveRules;

import chess.*;

import java.util.List;

public class BishopMoves implements MoveCalculator {
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition) {
        int[][] directions = {{-1, 1}, {1, 1}, {1, -1}, {-1, -1}};

        return MoveCalculator.possibleMoves(board, startPosition, directions);
    }
}

