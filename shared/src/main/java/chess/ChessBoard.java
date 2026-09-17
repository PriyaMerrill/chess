package chess;

import java.util.Arrays;
import java.util.Objects;

import static chess.ChessPiece.PieceType.*;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    //2D array for the board to have row and column in 2D form
    //an array of arrays
    //starting board so it is an array of all the Chess Pieces in the game
    private final ChessPiece[][] board;

    public ChessBoard() {
        //whole grid is set at once, all the values are null right now
        this.board = new ChessPiece[8][8];
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[position.getRow()-1][position.getColumn()-1];
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    //I use 3 loops
    //Pawns- 1 loop over columns 1-8 with two addPiece calls per round; 1 for white row 2 and 1 for black row 7
    //The other pieces are in an array
    //loops over columns 1-8 in two separate loops white row 1 and black row 8

    //All loops ended up looking the same so combined into one
    public void resetBoard() {
        ChessPiece.PieceType[] types = {ROOK, KNIGHT, BISHOP, QUEEN, KING, BISHOP, KNIGHT, ROOK};

        for (int i=1; i<=8; i++){
            addPiece(new ChessPosition(2,i), new ChessPiece(ChessGame.TeamColor.WHITE, PAWN));
            addPiece(new ChessPosition(7,i), new ChessPiece(ChessGame.TeamColor.BLACK, PAWN));

            addPiece(new ChessPosition(1,i), new ChessPiece(ChessGame.TeamColor.WHITE, types[i-1]));
            addPiece(new ChessPosition(8,i), new ChessPiece(ChessGame.TeamColor.BLACK, types[i-1]));
        }

    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }
}
