package MoveRules;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public class MoveCalculator {
    static boolean onBoard(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
    static List<ChessMove> generateMoves(ChessBoard board, ChessPosition startPosition, int[][] directions, boolean isLimited) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece startPiece = board.getPiece(startPosition);
        ChessGame.TeamColor team = startPiece.getTeamColor();


        return moves;
    }

}
