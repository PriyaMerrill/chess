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
        int endRow;

        if(myTeam == ChessGame.TeamColor.WHITE) {
            moveForward = 1;
            startRow = 2;
            endRow = 8;
        } else {
            moveForward = -1;
            startRow = 7;
            endRow = 1;
        }

        int row = position.getRow();
        int col = position.getColumn();

        int nextRow = row + moveForward;
        ChessPosition newSpot = new ChessPosition(nextRow, col);

        if ((nextRow >= 1 && nextRow <= 8) && (board.getPiece(newSpot) == null )){
            addPawnMove(movePawn, position, newSpot, endRow);

            //a pawn can move two spots on the first turn
            //row of the square two ahead positive for white negative for black
            int twoStep = row+moveForward*2;
            ChessPosition moveTwo = new ChessPosition(twoStep, col);
            if ((row == startRow) && (board.getPiece(moveTwo) == null)){
                addPawnMove(movePawn, position, moveTwo, endRow);
            }
        }
        pawnCapture(board, position, movePawn, myTeam, nextRow, endRow);

        return movePawn;
    }

    private void pawnCapture(ChessBoard board, ChessPosition position, Collection<ChessMove>movePawn, ChessGame.TeamColor myTeam, int nextRow, int promotionRow){
        int col = position.getColumn();
        int[] diagonals = {-1,1};
        for (int diagonal : diagonals){
            int captureCol = col + diagonal;
            if (captureCol >= 1 && captureCol <= 8){
                ChessPosition captureSpot = new ChessPosition(nextRow,captureCol);
                ChessPiece targetPiece = board.getPiece(captureSpot);

                if ((targetPiece != null) && (targetPiece.getTeamColor() != myTeam)){
                    addPawnMove(movePawn, position, captureSpot, promotionRow);
                }
            }
        }
    }

    private void addPawnMove(Collection<ChessMove> movePawn, ChessPosition start, ChessPosition end, int promotionRow){
        if (end.getRow() == promotionRow){
            movePawn.add(new ChessMove(start,end, ChessPiece.PieceType.QUEEN));
            movePawn.add(new ChessMove(start,end, ChessPiece.PieceType.BISHOP));
            movePawn.add(new ChessMove(start,end, ChessPiece.PieceType.KNIGHT));
            movePawn.add(new ChessMove(start,end, ChessPiece.PieceType.ROOK));
        } else {
            movePawn.add(new ChessMove(start, end, null));
        }
    }
}
