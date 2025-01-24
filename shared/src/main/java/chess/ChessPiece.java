package chess;

import chess.MoveRules.BishopMoves;
import chess.MoveRules.MoveCalculator;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final PieceType pieceType;
    private final ChessGame.TeamColor teamColor;
    private MoveCalculator pieceCalculator;
    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceType = type;
        this.teamColor = pieceColor;
        //this.pieceCalculator = calculator;

        switch (pieceType){
            case BISHOP -> {
                this.pieceCalculator = new BishopMoves();
                break;
            }
            default -> this.pieceCalculator = null;
        }
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        if (pieceCalculator == null) {
            return new ArrayList<>();
        }
        return pieceCalculator.calculateMove(board, this, myPosition);
    }
}
