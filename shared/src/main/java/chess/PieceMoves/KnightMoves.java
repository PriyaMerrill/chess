package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public class KnightMoves implements MovesCalculator {
    @Override
    public Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position) {
        boolean limit = true;
        int[][] directions = {{2,1}, {2,-1}, {-2,1}, {-2,-1}, {1,2}, {1,-2}, {-1,2}, {-1,-2}};
        return MovesCalculator.movePieces(board, position, directions, limit);
    }
}
