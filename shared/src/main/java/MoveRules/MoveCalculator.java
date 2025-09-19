package MoveRules;

import chess.*;

import java.util.ArrayList;
import java.util.List;

//centralized information that each piece can use
public interface MoveCalculator {
    static boolean onBoard(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }
    static List<ChessMove> generateMoves(ChessBoard board, ChessPosition startPosition, int[][] directions, boolean isLimited) {
        List<ChessMove> moves = new ArrayList<>();
        ChessPiece startPiece = board.getPiece(startPosition);
        ChessGame.TeamColor team = startPiece.getTeamColor();

        for (int[] direction : directions) {
            boolean pieceInPlace = false;
            int steps = 1;

            while (!pieceInPlace) {
                int endRow = startPosition.getRow() + direction[1] * steps;
                int endCol = startPosition.getColumn() + direction[0] * steps;
                ChessPosition endPosition = new ChessPosition(endRow, endCol);

                if (!onBoard(endPosition)) {
                    break;
                }

                ChessPiece endPiece = board.getPiece(endPosition);
                if (endPiece == null) {
                    moves.add(new ChessMove(startPosition, endPosition, null));
                } else if (endPiece.getTeamColor() != team) {
                    moves.add(new ChessMove(startPosition, endPosition, null));
                    pieceInPlace = true;
                } else {
                    pieceInPlace = true;
                }

                if (isLimited) {
                    pieceInPlace = true;;
                }

                steps++;
            }
        }

        return moves;
    }

}
