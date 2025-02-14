package chess;

import chess.MoveRules.MoveCalculator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    ChessBoard board;
    TeamColor teamTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK;

        public TeamColor nextTurn(){
            return (this == WHITE) ? BLACK : WHITE;
         }
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return List.of();
        }
        List<ChessMove> validMoves = new ArrayList<>();
        for (ChessMove move : piece.pieceMoves(board, startPosition)) {
            ChessBoard test = new ChessBoard();
            try {
                test.makeMove(move);
                if (!isInCheck(piece.getTeamColor(), test)) {
                    validMoves.add(move);
                }
            } catch (InvalidMoveException ignored) {

            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null) {
            throw new InvalidMoveException();
        }
        if (piece.getTeamColor() != teamTurn) {
            throw new InvalidMoveException();
        }
        if (!validMoves(move.getStartPosition()).contains(move)) {
            throw new InvalidMoveException();
        }
        board.makeMove(move);
        teamTurn = teamTurn.nextTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {

        return false;
    }

    private static boolean isInCheck(TeamColor teamColor, ChessBoard board) {
        ChessPosition kingSpot = findKing(teamColor, board);
        if (kingSpot == null) {
            return false;
        }
        for (ChessPosition position : getTeamPieces(teamColor.nextTurn(), board)){
            ChessPiece piece = board.getPiece(position);
            for (ChessMove move : piece.pieceMoves(board, kingSpot)) {
                if (move.getEndPosition().equals(kingSpot)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPos = findKing(teamColor, board);
        if (!isInCheck(teamColor)) {
            return false;
        }
        List<ChessPosition> teamPieces = getTeamPieces(teamColor, board);
        for (ChessPosition piecePos : teamPieces){
            ChessPiece piece = board.getPiece(piecePos);
            Collection<ChessMove> possibleMoves = piece.pieceMoves(board, piecePos);
            for (ChessMove move : possibleMoves){
                ChessBoard test = new ChessBoard(board);
                test.makeMove(move);
                if (!test.isInCheck(teamColor)) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);
                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }
                Collection<ChessMove> possibleMoves = piece.pieceMoves(board, position);
                if(possibleMoves != null && !possibleMoves.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
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
        List<ChessPosition> teamPieces = new ArrayList<ChessPosition>();
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
