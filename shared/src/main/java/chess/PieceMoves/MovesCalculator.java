package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.Collection;

//this will hold every piece's move logic
//not going to hold any individual piece logic just what the calculator can do
public interface MovesCalculator {
    Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position);


}
