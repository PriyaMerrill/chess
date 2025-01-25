package chess.MoveRules;

import chess.*;

import java.util.List;

public class PawnMoves implements MoveCalculator {
    public static List<ChessMove> makeMoves(ChessBoard board, ChessPosition startPosition){
        ChessPiece pawn = board.getPiece(startPosition);
        ChessGame.TeamColor team = pawn.getTeamColor();
        return MoveCalculator.pawn(board, startPosition, team);
    }
}
