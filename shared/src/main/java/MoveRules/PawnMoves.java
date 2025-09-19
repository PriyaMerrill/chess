package MoveRules;

import chess.*;

import java.util.List;

public class PawnMoves implements PawnCalculator{
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition){
        ChessPiece pawn = board.getPiece(startPosition);
        ChessGame.TeamColor team = pawn.getTeamColor();
        return PawnCalculator.pawn(board, startPosition, team);
    }
}
