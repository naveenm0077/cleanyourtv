package com.gathin.tvhome;

/**
 * The App library as a grid that reads left to right, row by row, and scrolls down, library_rows rows in view: which
 * card a D-pad key moves to, and how many rows and columns fit the screen.
 */
final class LibraryGrid {
    private LibraryGrid() {}

    static final int UP = 0;
    static final int DOWN = 1;
    /** Towards the start of a row: Left, or Right in a right-to-left layout. */
    static final int BACK = 2;
    /** Towards the end of a row. */
    static final int FORWARD = 3;
    static final int MAX_ROWS = 3;

    /** library_rows as asked for, kept to 1..MAX_ROWS and to the rows that fit; never below one row. */
    static int rows(int requested, int fit) {
        return Math.max(1, Math.min(Math.min(requested, MAX_ROWS), fit));
    }

    /** How many of size fit in available after first (nothing for first counts as none): the first takes first,
     *  each further one takes size. Used for rows (first = the one-row popup) and columns (first = 0). */
    static int fit(int available, int first, int size) {
        if (size <= 0 || available < first) return 1;
        return Math.max(1, (first > 0 ? 1 : 0) + (available - first) / size);
    }

    /**
     * The position a key moves focus to, or -1 when it stays put. Back and Forward stay in the row; Up and Down keep
     * the column. Down from a column the shorter last row does not reach lands on the last card, so every card stays
     * reachable; the edges of the grid hold focus instead of letting it leave the library.
     */
    static int move(int index, int count, int columns, int direction) {
        if (columns < 1 || index < 0 || index >= count) return -1;
        int column = index % columns;
        switch (direction) {
            case BACK:
                return column > 0 ? index - 1 : -1;
            case FORWARD:
                return column < columns - 1 && index + 1 < count ? index + 1 : -1;
            case UP:
                return index >= columns ? index - columns : -1;
            case DOWN:
                if (index + columns < count) return index + columns;
                return index / columns < (count - 1) / columns ? count - 1 : -1;
            default:
                return -1;
        }
    }
}
