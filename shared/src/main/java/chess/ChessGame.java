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
        //for each piece that might move:
        //make a copy board
        //remove piece from start pos and put it at end pos, check if promotion
        //if that piece team is not in check on the copy board add it to validMoves
        //return validMoves
        ChessPiece piece = board.getPiece(startPosition);
        if(piece == null){
            return null;
        }
        Collection<ChessMove> potentialMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

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

        actuallyMove(board, move);

        if(team == TeamColor.WHITE){
            team = TeamColor.BLACK;
        } else {
            team = TeamColor.WHITE;
        }
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
    private void actuallyMove (ChessBoard mainBoard, ChessMove move){
        ChessPiece piece = mainBoard.getPiece(move.getStartPosition());
        mainBoard.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null){
            ChessPiece promoPiece = new ChessPiece(piece.getTeamColor(), move.getPromotionPiece());
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
