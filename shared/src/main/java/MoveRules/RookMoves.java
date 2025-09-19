package MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.List;

public class RookMoves implements MoveCalculator{
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition){
        int [][] directions = {{0, 1}, {0, -1}, {1, 0}, {-1, 0}};
        return MoveCalculator.generateMoves(board, startPosition, directions,false);
    }
}
