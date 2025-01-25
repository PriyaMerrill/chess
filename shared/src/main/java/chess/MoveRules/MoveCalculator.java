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

        if (startPiece == null) {
            throw new IllegalArgumentException("No piece there.");
        }

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

}
