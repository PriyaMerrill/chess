package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A class that manages a chess game, making moves on a board.
 */
public class ChessGame {

    private ChessBoard board;
    private TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return The team whose turn it is.
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Sets which team's turn it is.
     * @param team The team whose turn it is.
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the two possible teams in a chess game.
     */
    public enum TeamColor {
        WHITE, BLACK;

        public TeamColor nextTurn() {
            return (this == WHITE) ? BLACK : WHITE;
        }
    }

    /**
     * Gets all valid moves for a piece at the given location.
     * @param startPosition The piece's position.
     * @return A set of valid moves for the requested piece.
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) return List.of();

        List<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            ChessBoard testBoard = new ChessBoard();
            try {
                testBoard.makeMove(move);
                if (!isInCheck(piece.getTeamColor(), testBoard)) {
                    validMoves.add(move);
                }
            } catch (InvalidMoveException ignored) {
            }
        }
        return validMoves;
    }


    /**
     * Executes a move in the chess game.
     * @param move The move to be performed.
     * @throws InvalidMoveException If the move is invalid.
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) throw new InvalidMoveException();
        if (piece.getTeamColor() != teamTurn) throw new InvalidMoveException();

        Collection<ChessMove> possibleMoves = validMoves(move.getStartPosition());
        if (!possibleMoves.contains(move)) throw new InvalidMoveException();
        ChessBoard testBoard = new ChessBoard();
        testBoard.makeMove(move);

        if (isInCheck(teamTurn, testBoard)) {
            throw new InvalidMoveException();
        }
        board.makeMove(move);
        if (move.getPromotionPiece() != null) {
            board.promotePawn(move.getEndPosition(), move.getPromotionPiece());
        }
        teamTurn = teamTurn.nextTurn();
    }



    /**
     * Checks if the given team is in check.
     * @param teamColor The team to check.
     * @return True if the specified team is in check.
     */
    public boolean isInCheck(TeamColor teamColor) {
        return isInCheck(teamColor, board);
    }

    private boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingSpot = findKing(teamColor, board);
        if (kingSpot == null) return false;

        TeamColor opponent = teamColor.nextTurn();
        for (ChessPosition pos : getTeamPieces(opponent, board)) {
            ChessPiece piece = board.getPiece(pos);
            if (piece != null) {
                for (ChessMove move : piece.pieceMoves(board, pos)) {
                    if (move.getEndPosition().equals(kingSpot)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate.
     * @param teamColor The team to check.
     * @return True if the team is in checkmate.
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        return isInCheck(teamColor) && noValidMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate.
     * @param teamColor The team to check.
     * @return True if the team is in stalemate.
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return !isInCheck(teamColor) && noValidMoves(teamColor);
    }

    private boolean noValidMoves(TeamColor teamColor) {
        for (ChessPosition position : getTeamPieces(teamColor, board)) {
            if (!validMoves(position).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard.
     * @param board The new board.
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard.
     * @return The chessboard.
     */
    public ChessBoard getBoard() {
        return board;
    }

    private ChessPosition findKing(TeamColor teamColor, ChessBoard board) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getPieceType() == ChessPiece.PieceType.KING && piece.getTeamColor() == teamColor) {
                    return position;
                }
            }
        }
        return null;
    }

    private List<ChessPosition> getTeamPieces(TeamColor teamColor, ChessBoard board) {
        List<ChessPosition> teamPieces = new ArrayList<>();
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    teamPieces.add(position);
                }
            }
        }
        return teamPieces;
    }
}
