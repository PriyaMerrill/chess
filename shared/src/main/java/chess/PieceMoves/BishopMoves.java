package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;
import java.util.Collection;

public class BishopMoves implements MovesCalculator{
    @Override
    public Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position){
        boolean limit = false;
        int[][] directions = {{1,1}, {1,-1}, {-1,1}, {-1,-1}};
        return MovesCalculator.movePieces(board, position, directions, limit);
    }
}
