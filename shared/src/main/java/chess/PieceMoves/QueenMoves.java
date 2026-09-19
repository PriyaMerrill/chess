package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public class QueenMoves implements MovesCalculator{
    @Override
    public Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position) {
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}, {1,0}, {-1,0}, {0,1}, {0,-1}};
        return MovesCalculator.slidePieces(board, position, directions);
    }
}
