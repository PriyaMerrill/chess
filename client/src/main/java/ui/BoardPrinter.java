package ui;

import static ui.EscapeSequences.*;

public class BoardPrinter {
    private static final String[] WHITE_COLUMNS = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final String[] BLACK_COLUMNS = {"h", "g", "f", "e", "d", "c", "b", "a"};

    private static final String[][] START_BOARD = {
            {"R", "N", "B", "Q", "K", "B", "N", "R"},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {" ", " ", " ", " ", " ", " ", " ", " "},
            {"P", "P", "P", "P", "P", "P", "P", "P"},
            {"R", "N", "B", "Q", "K", "B", "N", "R"}
    };
    private static final boolean[][] BLACK_PIECE = {
            {true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true},
            {false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false},
            {false, false, false, false, false, false, false, false}
    };

    public static void printBoard(boolean black) {
        System.out.println();

        String[] columns = black ? BLACK_COLUMNS : WHITE_COLUMNS;
        int startRow = black ? 0 : 7;
        int endRow = black ? 8 : -1;
        int rowMove = black ? 1 : -1;

        System.out.print("   ");
        for (String col : columns) {
            System.out.print(" " + col + " ");
        }
        System.out.println();

        int rowNum = black ? 1 : 8;
        for (int row = startRow; row != endRow; row += rowMove) {
            System.out.print(" " + rowNum + " ");
            for (int col = 0; col < 8; col++) {
                int realCol = black ? 7 - col : col;
                boolean lightSquare = (row + realCol) % 2 == 1;

                String color = lightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
                String piece = START_BOARD[row][realCol];
                String pieceColor;

                if (piece.equals(" ")) {
                    pieceColor = "";
                } else if (BLACK_PIECE[row][realCol]) {
                    pieceColor = SET_TEXT_COLOR_BLUE;
                } else {
                    pieceColor = SET_TEXT_COLOR_RED;
                }

                System.out.print(color + pieceColor + " " + piece + " " + RESET_BG_COLOR + RESET_TEXT_COLOR);
            }

            System.out.println(" " + rowNum);
            rowNum += black ? 1 : -1;
        }

        System.out.print("   ");
        for (String col : columns) {
            System.out.print(" " + col + " ");
        }

        System.out.println();
        System.out.println();
    }
}
