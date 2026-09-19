package chess.PieceMoves;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;

//this will hold every piece's move logic
public interface MovesCalculator {
    Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position);

    static Collection<ChessMove> slidePieces(ChessBoard board, ChessPosition position, int[][] directions){
        Collection<ChessMove> moves = new ArrayList<>();
        ChessGame.TeamColor myTeam = board.getPiece(position).getTeamColor();

        //loop through possible moves that are in piece direction rules
        for (int[] direction : directions){
            int newRow = position.getRow();
            int newCol = position.getColumn();

            newRow += direction[0];
            newCol += direction[1];
            //keep the piece on the board
            while(newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
                ChessPosition newPos = new ChessPosition(newRow, newCol);
                ChessPiece endPiece = board.getPiece(newPos);
                if (endPiece==null){
                    moves.add(new ChessMove(position, newPos, null));
                } else if (endPiece.getTeamColor() == myTeam) {
                    break;
                } else {
                    moves.add(new ChessMove(position, newPos, null));
                    break;
                }
                newRow += direction[0];
                newCol += direction[1];
            }
        }
        return moves;
    }


}
