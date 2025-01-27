package chess.MoveRules;

import chess.*;

import java.util.List;

public class PawnMoves implements pawnCalculator {
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition){
        ChessPiece pawn = board.getPiece(startPosition);
        ChessGame.TeamColor team = pawn.getTeamColor();
        return pawnCalculator.pawn(board, startPosition, team);
    }
}
