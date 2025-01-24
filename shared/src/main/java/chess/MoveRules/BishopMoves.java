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

        List<ChessMove> topRightMoves = new ArrayList<>(validMove(board, piece, position, 1, 1));
        List<ChessMove> topLeftMoves = new ArrayList<>(validMove(board, piece, position, 1, -1));
        List<ChessMove> bottomRightMoves = new ArrayList<>(validMove(board, piece, position, -1, 1));
        List<ChessMove> bottomLeftMoves = new ArrayList<>(validMove(board, piece, position, -1, -1));

        List<ChessMove> movesInOrder = new ArrayList<>();
        movesInOrder.addAll(topRightMoves);
        movesInOrder.addAll(topLeftMoves);
        movesInOrder.addAll(bottomRightMoves);
        movesInOrder.addAll(bottomLeftMoves);

        System.out.println("Final ordered moves:");
        for (ChessMove move : movesInOrder) {
            System.out.println("Move: Start = " + move.getStartPosition() + ", End = " + move.getEndPosition());
        }


        return movesInOrder;
    }
        /*
        List<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(validMove(board, piece, position, 1, 1));
        possibleMoves.addAll(validMove(board, piece, position, 1, -1));
        possibleMoves.addAll(validMove(board, piece, position, -1, 1));
        possibleMoves.addAll(validMove(board, piece, position, -1, -1));

        possibleMoves.sort(
                Comparator.comparing((ChessMove move) -> move.getEndPosition().getRow())
                        .thenComparing(move -> move.getEndPosition().getColumn())
        );

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
        possibleSpot.addAll(testPositions);
    }

         */
}

