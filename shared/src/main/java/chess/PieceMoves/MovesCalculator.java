package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

//this will hold every piece's move logic
public interface MovesCalculator {
    Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position);

    static Collection<ChessMove> slidePieces(ChessBoard board, ChessPosition position, int[][] directions){
        Collection<ChessMove> moves = new ArrayList<>();
        return moves;
    }


}
