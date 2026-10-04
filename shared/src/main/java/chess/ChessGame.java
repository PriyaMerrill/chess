package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    //this is what the game remembers
    private ChessBoard board;
    private ChessGame.TeamColor team;
    private ChessMove lastMove = null;

    //castling variables
    private boolean whiteKingMove = false;
    private boolean blackKingMove = false;
    private boolean whiteLeftRookMove =false;
    private boolean blackLeftRookMove =false;
    private boolean whiteRightRookMove =false;
    private boolean blackRightRookMove =false;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.team = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        //get piece and return null if no piece
        //get moves for piece and have a list
        //check if en passant or castling can be added
        //for each piece that might move:
        //make a copy board and actually move the piece
        //if that piece team is not in check on the copy board add it to validMoves
        //return validMoves

        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return null;
        }

        Collection<ChessMove> potentialMoves = new ArrayList<>(piece.pieceMoves(board, startPosition));
        Collection<ChessMove> validMoves = new ArrayList<>();

        //check for en passant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN && lastMove != null){
            ChessPosition lastStart = lastMove.getStartPosition();
            ChessPosition lastEnd = lastMove.getEndPosition();
            ChessPiece lastPiece = board.getPiece(lastEnd);

            if (lastPiece != null
                    && lastPiece.getPieceType() == ChessPiece.PieceType.PAWN
                    && lastPiece.getTeamColor() != piece.getTeamColor()
                    && lastStart.getColumn() == lastEnd.getColumn()
                    && Math.abs(lastStart.getRow() - lastEnd.getRow()) == 2
                    && lastEnd.getRow() == startPosition.getRow()
                    && Math.abs(lastEnd.getColumn() - startPosition.getColumn()) == 1) {

                int direction;
                if (piece.getTeamColor() == TeamColor.WHITE){
                    direction = 1;
                } else {
                    direction = -1;
                }

                ChessPosition enPassant = new ChessPosition(
                        startPosition.getRow() + direction,
                        lastEnd.getColumn()
                );

                if (board.getPiece(enPassant) == null){
                    potentialMoves.add(new ChessMove(startPosition, enPassant, null));
                }
            }
        }

        //check for castling
        if (piece.getPieceType() == ChessPiece.PieceType.KING) {
            int row;
            boolean kingMoved;

            if (piece.getTeamColor() == TeamColor.WHITE) {
                row = 1;
                kingMoved = whiteKingMove;
            } else {
                row = 8;
                kingMoved = blackKingMove;
            }

            //king has to be at start spot, not moved before, and not in check
            if (!kingMoved
                    && startPosition.getRow() == row
                    && startPosition.getColumn() == 5
                    && !isInCheck(piece.getTeamColor())) {

                //check left side castle
                boolean leftRookMoved;
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    leftRookMoved = whiteLeftRookMove;
                } else {
                    leftRookMoved = blackLeftRookMove;
                }

                ChessPosition leftRookPos = new ChessPosition(row, 1);
                ChessPiece leftRook = board.getPiece(leftRookPos);

                if (!leftRookMoved
                        && leftRook != null
                        && leftRook.getPieceType() == ChessPiece.PieceType.ROOK
                        && leftRook.getTeamColor() == piece.getTeamColor()
                        && board.getPiece(new ChessPosition(row, 2)) == null
                        && board.getPiece(new ChessPosition(row, 3)) == null
                        && board.getPiece(new ChessPosition(row, 4)) == null) {

                    //check if king can move through this spot without check
                    ChessBoard boardCopy = board.copyOfBoard();
                    ChessMove oneStep = new ChessMove(
                            startPosition,
                            new ChessPosition(row, 4),
                            null
                    );

                    actuallyMove(boardCopy, oneStep);

                    if (!isInCheck(piece.getTeamColor(), boardCopy)) {
                        potentialMoves.add(new ChessMove(
                                startPosition,
                                new ChessPosition(row, 3),
                                null
                        ));
                    }
                }

                //check right side castle
                boolean rightRookMoved;
                if (piece.getTeamColor() == TeamColor.WHITE) {
                    rightRookMoved = whiteRightRookMove;
                } else {
                    rightRookMoved = blackRightRookMove;
                }

                ChessPosition rightRookPos = new ChessPosition(row, 8);
                ChessPiece rightRook = board.getPiece(rightRookPos);

                if (!rightRookMoved
                        && rightRook != null
                        && rightRook.getPieceType() == ChessPiece.PieceType.ROOK
                        && rightRook.getTeamColor() == piece.getTeamColor()
                        && board.getPiece(new ChessPosition(row, 6)) == null
                        && board.getPiece(new ChessPosition(row, 7)) == null) {

                    //check if king can move through this spot without check
                    ChessBoard boardCopy = board.copyOfBoard();
                    ChessMove oneStep = new ChessMove(
                            startPosition,
                            new ChessPosition(row, 6),
                            null
                    );

                    actuallyMove(boardCopy, oneStep);

                    if (!isInCheck(piece.getTeamColor(), boardCopy)) {
                        potentialMoves.add(new ChessMove(
                                startPosition,
                                new ChessPosition(row, 7),
                                null
                        ));
                    }
                }
            }
        }

        //check every potential move to make sure it doesnt leave the king in check
        for(ChessMove move : potentialMoves){
            ChessBoard boardCopy = board.copyOfBoard();
            actuallyMove(boardCopy, move);

            if (!isInCheck(piece.getTeamColor(), boardCopy)){
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //get the piece at the move start spot
        //if there is no piece there throw exception
        //if the piece doesn't belong to the team whose turn it is throw exception
        //get all valid moves for the piece
        //if the move is a promotion create promoted piece and put it at the end spot
        //if it isnt put the original piece at the end spot
        //switch team turns

        ChessPiece piece = board.getPiece(move.getStartPosition());
        if (piece == null || piece.getTeamColor() != team) {
            throw new InvalidMoveException();
        }
        Collection<ChessMove> moves = validMoves(move.getStartPosition());

        if (moves == null || !moves.contains(move)){
            throw new InvalidMoveException();
        }

        if(piece.getPieceType() == ChessPiece.PieceType.KING){
            if (piece.getTeamColor() == TeamColor.WHITE){
                whiteKingMove = true;
            } else {
                blackKingMove = true;
            }
        }

        if (piece.getPieceType() == ChessPiece.PieceType.ROOK) {
            int startRow = move.getStartPosition().getRow();
            int startCol = move.getStartPosition().getColumn();

            if (piece.getTeamColor() == TeamColor.WHITE && startRow == 1) {
                if (startCol == 1) {
                    whiteLeftRookMove = true;
                } else if (startCol == 8) {
                    whiteRightRookMove = true;
                }
            } else if (piece.getTeamColor() == TeamColor.BLACK && startRow == 8) {
                if (startCol == 1) {
                    blackLeftRookMove = true;
                } else if (startCol == 8) {
                    blackRightRookMove = true;
                }
            }
        }

        actuallyMove(board, move);

        if(team == TeamColor.WHITE){
            team = TeamColor.BLACK;
        } else {
            team = TeamColor.WHITE;
        }
        lastMove = move;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */

    //still have a public version for other things to use
    public boolean isInCheck (TeamColor teamColor){
        return isInCheck(teamColor, board);
    }

    //change to private and also take a board so it checks the copy board and not the real one
    private boolean isInCheck(TeamColor teamColor, ChessBoard checkBoard) {
        ChessPosition kingPos = findKing(teamColor, checkBoard);
        if (kingPos == null){
            return false;
        }
        //loop through every row and col
        //create position and get piece
        //if there is a piece on the other team
        //get all moves for that other team piece
        //loop through those moves
        //if a move ends at the king's position is in check is true
        for(int row = 1; row <= 8; row++){
            for(int col=1; col<=8; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece pieceThere = checkBoard.getPiece(pos);
                if(pieceThere != null && pieceThere.getTeamColor() != teamColor){
                    Collection<ChessMove> moves = pieceThere.pieceMoves(checkBoard, pos);
                    for (ChessMove move : moves){
                        if (move.getEndPosition().equals(kingPos)){
                            return true;
                        }
                    }

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
        //if team is not in check it isnt in checkmate
        //look through every square on the board
        //look at pieces on this team
        //if any piece has a valid move it isnt checkmate
        //if in check and no valid moves return true

        if (!isInCheck(teamColor)){
            return false;
        }
        for(int row = 1; row <= 8; row++){
            for (int col=1; col<=8; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if(piece != null && piece.getTeamColor()==teamColor){
                    Collection<ChessMove> moves = validMoves(pos);

                    if(moves != null && !moves.isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //if team is in check it can't be stalemate
        //look through every square on the board
        //only look at pieces belonging to this team
        //if any piece has a valid move it isnt stalemate
        //not in check and no valid moves return true

        if (isInCheck(teamColor)){
            return false;
        }
        for (int row = 1; row <= 8; row++){
            for (int col=1; col<=8; col++){
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                if(piece != null && piece.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(pos);
                    if (moves != null && !moves.isEmpty()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        lastMove = null;

        whiteKingMove = false;
        blackKingMove = false;

        whiteLeftRookMove = false;
        whiteRightRookMove = false;

        blackLeftRookMove = false;
        blackRightRookMove = false;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    //method for looking for the king on the board
    private ChessPosition findKing(ChessGame.TeamColor teamColor, ChessBoard checkBoard){
        //loop through rows and columns
        //position for square looked at and get piece there
        //if there is a piece see if it is a king and matches team
        //return this position if it does
        for(int row=1; row<=8; row++){
            for(int col=1; col<=8; col++){
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece pieceThere = checkBoard.getPiece(position);

                if(pieceThere != null && pieceThere.getPieceType() == ChessPiece.PieceType.KING && pieceThere.getTeamColor() == teamColor){
                    return position;
                }
            }
        }

        //no king was found
        return null;
    }

    //applies a move to a board so the same logic is reused
    //gets the piece, makes it start position clear, moves it to end position
    //deals with promotion by making the promoted piece
    private void actuallyMove(ChessBoard mainBoard, ChessMove move){
        ChessPiece piece = mainBoard.getPiece(move.getStartPosition());
        ChessPiece endPiece = mainBoard.getPiece(move.getEndPosition());
        //if pawn moves diagonal to empty spot remove pawn for en passant
        if (piece.getPieceType() == ChessPiece.PieceType.PAWN
                && move.getStartPosition().getColumn() != move.getEndPosition().getColumn()
                && endPiece == null) {

            ChessPosition capturePawn = new ChessPosition(
                    move.getStartPosition().getRow(),
                    move.getEndPosition().getColumn()
            );

            mainBoard.addPiece(capturePawn, null);
        }
        //if king moves two columns it is castling so move the rook too
        if (piece.getPieceType() == ChessPiece.PieceType.KING
                && Math.abs(move.getStartPosition().getColumn()
                - move.getEndPosition().getColumn()) == 2) {

            int row = move.getStartPosition().getRow();
            //castle left
            if (move.getEndPosition().getColumn() == 3) {
                ChessPosition rookStart = new ChessPosition(row, 1);
                ChessPosition rookEnd = new ChessPosition(row, 4);
                ChessPiece rook = mainBoard.getPiece(rookStart);

                mainBoard.addPiece(rookStart, null);
                mainBoard.addPiece(rookEnd, rook);
            }
            //castle right
            else if (move.getEndPosition().getColumn() == 7) {
                ChessPosition rookStart = new ChessPosition(row, 8);
                ChessPosition rookEnd = new ChessPosition(row, 6);
                ChessPiece rook = mainBoard.getPiece(rookStart);

                mainBoard.addPiece(rookStart, null);
                mainBoard.addPiece(rookEnd, rook);
            }
        }
        mainBoard.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null){
            ChessPiece promoPiece = new ChessPiece(
                    piece.getTeamColor(),
                    move.getPromotionPiece()
            );
            mainBoard.addPiece(move.getEndPosition(), promoPiece);
        } else {
            mainBoard.addPiece(move.getEndPosition(), piece);
        }
    }
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return Objects.equals(board, chessGame.board) && team == chessGame.team;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, team);
    }

}
