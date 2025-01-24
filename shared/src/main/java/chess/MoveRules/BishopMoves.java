package chess.MoveRules;

import chess.ChessBoard;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.List;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

public class BishopMoves extends MoveCalculator {
    @Override
    public Collection<ChessMove> calculateMove(ChessBoard board, ChessPiece piece, ChessPosition position) {
        List<ChessMove> possibleMoves = new ArrayList<>();
        addMove(possibleMoves, validMove(board, piece, position, 1, 1));
        addMove(possibleMoves, validMove(board, piece, position, 1, -1));
        addMove(possibleMoves, validMove(board, piece, position, -1, 1));
        addMove(possibleMoves, validMove(board, piece, position, -1, -1));

        possibleMoves.sort(
                Comparator.comparing((ChessMove move) -> move.getEndPosition().getRow())
                        .thenComparing(move -> move.getEndPosition().getColumn())
        );

        return possibleMoves;
    }

    private void addMove(Collection<ChessMove> possibleSpot, Collection<ChessMove> testPositions) {
        for (ChessMove movePos : testPositions) {
            possibleSpot.add(movePos);
        }
    }
}

