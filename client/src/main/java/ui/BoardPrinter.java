package ui;

import chess.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static ui.EscapeSequences.*;

public class BoardPrinter {

    public static void printBoard(ChessGame game, boolean blackPerspective) {
        printBoardWithHighlights(game, blackPerspective, null, null);
    }

    public static void printBoard(boolean blackPerspective) {
        ChessGame game = new ChessGame();
        printBoard(game, blackPerspective);
    }

    public static void printBoardWithHighlights(ChessGame game, boolean blackPerspective, 
                                                  ChessPosition selectedPos, Collection<ChessMove> validMoves) {
        System.out.println();
        
        ChessBoard board = game.getBoard();
        
        Set<ChessPosition> highlightSquares = new HashSet<>();
        if (validMoves != null) {
            for (ChessMove move : validMoves) {
                highlightSquares.add(move.getEndPosition());
            }
        }

        String[] columns = blackPerspective ? 
            new String[]{"h", "g", "f", "e", "d", "c", "b", "a"} :
            new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};

        int startRow = blackPerspective ? 1 : 8;
        int endRow = blackPerspective ? 9 : 0;
        int rowStep = blackPerspective ? 1 : -1;

        System.out.print("   ");
        for (String col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();

        for (int row = startRow; row != endRow; row += rowStep) {
            System.out.print(" " + row + " ");

            int startCol = blackPerspective ? 8 : 1;
            int endCol = blackPerspective ? 0 : 9;
            int colStep = blackPerspective ? -1 : 1;

            for (int col = startCol; col != endCol; col += colStep) {
                ChessPosition pos = new ChessPosition(row, col);
                boolean isLightSquare = (row + col) % 2 == 0;
                boolean isHighlighted = highlightSquares.contains(pos);
                boolean isSelected = selectedPos != null && selectedPos.equals(pos);

                String bgColor;
                if (isSelected) {
                    bgColor = SET_BG_COLOR_YELLOW;
                } else if (isHighlighted) {
                    bgColor = isLightSquare ? SET_BG_COLOR_GREEN : SET_BG_COLOR_DARK_GREEN;
                } else {
                    bgColor = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                }

                ChessPiece piece = board.getPiece(pos);
                String pieceStr = getPieceString(piece);
                String pieceColor = "";
                if (piece != null) {
                    pieceColor = piece.getTeamColor() == ChessGame.TeamColor.WHITE ? 
                        SET_TEXT_COLOR_RED : SET_TEXT_COLOR_BLUE;
                }

                System.out.print(bgColor + pieceColor + " " + pieceStr + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            }

            System.out.println(" " + row);
        }

        System.out.print("   ");
        for (String col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();
        System.out.println();
    }

    private static String getPieceString(ChessPiece piece) {
        if (piece == null) {
            return " ";
        }
        return switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case ROOK -> "R";
            case PAWN -> "P";
        };

    }
}
