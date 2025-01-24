package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.ArrayList;
import java.util.Collection;

public abstract class MoveCalculator {
    public abstract Collection<ChessMove> calculateMove(ChessBoard board, ChessPiece piece, ChessPosition position);

    protected Collection<ChessMove> validMove(ChessBoard board, ChessPiece piece, ChessPosition startPosition, int rowMoves, int colMoves) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();

        while (true) {
            startRow += rowMoves;
            startCol += colMoves;

            if (startRow < 1 || startRow > 8 || startCol < 1 || startCol > 8) {
                System.out.println("Out of bounds: [" + startRow + "," + startCol + "]");
                break;
            }
            ChessPosition endPosition = new ChessPosition(startRow, startCol);
            ChessPiece pieceInSpot = board.getPiece(endPosition);

            if (pieceInSpot == null) {
                possibleMoves.add(new ChessMove(startPosition, endPosition, null));
            } else {
                if (!pieceInSpot.getTeamColor().equals(piece.getTeamColor())) {
                    possibleMoves.add(new ChessMove(startPosition, endPosition, null));
                }
                break;
            }
        }
        return possibleMoves;
    }


}
