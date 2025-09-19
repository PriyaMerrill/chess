package MoveRules;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public interface PawnCalculator extends MoveCalculator{
    static List<ChessMove> pawn(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor team){
        int pawnDirection;
        List<ChessMove> moves = new ArrayList<>();
        if (team == ChessGame.TeamColor.WHITE) {
            pawnDirection = 1;
        } else {
            pawnDirection = -1;
        }

        ChessPosition movement = new ChessPosition(startPosition.getRow() + pawnDirection, startPosition.getColumn());
        if (MoveCalculator.onBoard(movement) && board.getPiece(movement) == null) {
            pawnMove(moves, startPosition, movement, team, board);
        }

        if ((team == ChessGame.TeamColor.WHITE && startPosition.getRow() == 2) || (team == ChessGame.TeamColor.BLACK && startPosition.getRow() == 7)){
            ChessPosition secondMovement = new ChessPosition(startPosition.getRow() + 2 * pawnDirection, startPosition.getColumn());
            if (MoveCalculator.onBoard(secondMovement) && board.getPiece(secondMovement) == null && board.getPiece(movement) == null){
                moves.add(new ChessMove(startPosition, secondMovement, null));
            }
        }

        int[][] pawnCaptures = {{pawnDirection, -1}, {pawnDirection, 1}};
        for (int[] capture : pawnCaptures) {
            ChessPosition capturePosition = new ChessPosition(startPosition.getRow() + capture[0], startPosition.getColumn() + capture[1]);
            if (MoveCalculator.onBoard(capturePosition)){
                ChessPiece endPiece = board.getPiece(capturePosition);
                if (endPiece != null && endPiece.getTeamColor() != team) {
                    pawnMove(moves, startPosition, capturePosition, team, board);
                }
            }
        }
        return moves;
    }

    private static void pawnMove(List<ChessMove> moves, ChessPosition startPosition, ChessPosition endPosition, ChessGame.TeamColor team, ChessBoard board){
        int promo;
        if (team == ChessGame.TeamColor.WHITE) {
            promo = 8;
        } else {
            promo = 1;
        }

        if (endPosition.getRow() == promo) {
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.QUEEN));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.KNIGHT));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.ROOK));
            moves.add(new ChessMove(startPosition, endPosition, ChessPiece.PieceType.BISHOP));
        } else {
            moves.add(new ChessMove(startPosition, endPosition, null));
        }
    }
}
