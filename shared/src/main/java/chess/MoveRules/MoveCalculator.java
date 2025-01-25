package chess.MoveRules;

import chess.*;

import java.util.ArrayList;
import java.util.List;

public interface MoveCalculator {
    static List<ChessMove> calculateMoves(ChessBoard board, ChessPosition position) {
        return new ArrayList<>();
    }

    static boolean onBoard(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    static List<ChessMove> possibleMoves(ChessBoard board, ChessPosition startPosition, int[][] directions) {
        List<ChessMove> moves = new ArrayList<>();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        ChessPiece startPiece = board.getPiece(startPosition);
        ChessGame.TeamColor team = startPiece.getTeamColor();

        for (int[] direction : directions) {
            boolean pieceInPlace = false;
            int steps = 1;

            while (!pieceInPlace) {
                int endRow = startRow + direction[1] * steps;
                int endCol = startCol + direction[0] * steps;
                ChessPosition endPosition = new ChessPosition(endRow, endCol);

                if (!onBoard(endPosition)) {
                    pieceInPlace = true;
                } else {
                    ChessPiece endPiece = board.getPiece(endPosition);
                    if (endPiece == null) {
                        moves.add(new ChessMove(startPosition, endPosition, null));
                    } else if (endPiece.getTeamColor() != team) {
                        moves.add(new ChessMove(startPosition, endPosition, null));
                        pieceInPlace = true;
                    } else {
                        pieceInPlace = true;
                    }
                }
                steps++;
            }
        }
        return moves;
    }

    static List<ChessMove> limitedMoves(ChessBoard board, ChessPosition startPosition, int[][] directions){
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece startPiece = board.getPiece(startPosition);

        ChessGame.TeamColor team = startPiece.getTeamColor();

        for (int[] direction : directions) {
            int endRow = startPosition.getRow() + direction[1];
            int endCol = startPosition.getColumn() + direction[0];
            ChessPosition endPosition = new ChessPosition(endRow, endCol);

            if (onBoard(endPosition)) {
                ChessPiece endPiece = board.getPiece(endPosition);
                if (endPiece == null) {
                    moves.add(new ChessMove(startPosition, endPosition, null));
                } else if (endPiece.getTeamColor() != team) {
                    moves.add(new ChessMove(startPosition, endPosition, null));
                }
            }
        }
        return moves;
    }

    static List<ChessMove> pawn(ChessBoard board, ChessPosition startPosition, ChessGame.TeamColor team){
        int pawnDirection;
        List<ChessMove> moves = new ArrayList<>();
        if (team == ChessGame.TeamColor.WHITE) {
            pawnDirection = 1;
        } else {
            pawnDirection = -1;
        }

        ChessPosition movement = new ChessPosition(startPosition.getRow() + pawnDirection, startPosition.getColumn());
        if (onBoard(movement) && board.getPiece(movement) == null) {
            pawnMove(moves, startPosition, movement, team, board);
        }

        if ((team == ChessGame.TeamColor.WHITE && startPosition.getRow() == 2) || (team == ChessGame.TeamColor.BLACK && startPosition.getRow() == 7)){
            ChessPosition secondMovement = new ChessPosition(startPosition.getRow() + 2 * pawnDirection, startPosition.getColumn());
            if (onBoard(secondMovement) && board.getPiece(secondMovement) == null && board.getPiece(movement) == null){
                moves.add(new ChessMove(startPosition, secondMovement, null));
            }
        }

        int[][] pawnCaptures = {{pawnDirection, -1}, {pawnDirection, 1}};
        for (int[] capture : pawnCaptures) {
            ChessPosition capturePosition = new ChessPosition(startPosition.getRow() + capture[0], startPosition.getColumn() + capture[1]);
            if (onBoard(capturePosition)){
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
