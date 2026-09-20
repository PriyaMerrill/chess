package chess.PieceMoves;

import chess.*;

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

            //a pawn can move two spots on the first turn
            //row of the square two ahead positive for white negative for black
            int twoStep = row+moveForward*2;
            ChessPosition moveTwo = new ChessPosition(twoStep, col);
            if ((row == startRow) && (board.getPiece(moveTwo) == null)){
                movePawn.add(new ChessMove(position, moveTwo, null));
            }
        }
        pawnCapture(board, position, movePawn, myTeam, nextRow);

        return movePawn;
    }

    private void pawnCapture(ChessBoard board, ChessPosition position, Collection<ChessMove>movePawn, ChessGame.TeamColor myTeam, int nextRow){
        int col = position.getColumn();
        int[] diagonals = {-1,1};
        for (int diagonal : diagonals){
            int captureCol = col + diagonal;
            if (captureCol >= 1 && captureCol <= 8){
                ChessPosition captureSpot = new ChessPosition(nextRow,captureCol);
                ChessPiece targetPiece = board.getPiece(captureSpot);

                if ((targetPiece != null) && (targetPiece.getTeamColor() != myTeam)){
                    movePawn.add(new ChessMove(position, captureSpot, null));
                }
            }
        }
    }
}
