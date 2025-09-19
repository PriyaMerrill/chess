package MoveRules;

import chess.ChessPosition;

public class MoveCalculator {
    static boolean onBoard(ChessPosition position){
        int row = position.getRow();
        int col = position.getColumn();
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

}
