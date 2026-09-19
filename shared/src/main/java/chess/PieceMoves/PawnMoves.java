package chess.PieceMoves;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public class PawnMoves implements MovesCalculator{
    @Override
    public Collection<ChessMove> makeMoves(ChessBoard board, ChessPosition position){
        Collection<ChessMove> movePawn = new ArrayList<>();
        ChessGame.TeamColor myTeam = board.getPiece(position).getTeamColor();
        int moveForward;
        int startRow;

        if(myTeam == ChessGame.TeamColor.WHITE) {
            moveForward = 1;
            startRow = 2;
        } else {
            moveForward = -1;
            startRow = 7;
        }

        int row = position.getRow();
        int col = position.getColumn();

        int nextRow = row + moveForward;
        ChessPosition newSpot = new ChessPosition(nextRow, col);

        if ((nextRow >= 1 && nextRow <= 8) && (board.getPiece(newSpot) == null )){
            movePawn.add(new ChessMove(position, newSpot, null));
        }

        return movePawn;
    }
}
