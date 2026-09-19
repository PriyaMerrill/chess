package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public class KingMoves implements MovesCalculator{
    @Override
    public Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position) {
        boolean limit = true;
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1,0}, {-1,0}, {0,1}, {0,-1}};
        return MovesCalculator.movePieces(board, position, directions, limit);
    }
}
