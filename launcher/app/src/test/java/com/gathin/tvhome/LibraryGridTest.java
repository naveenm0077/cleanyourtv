package com.gathin.tvhome;

import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static com.gathin.tvhome.LibraryGrid.BACK;
import static com.gathin.tvhome.LibraryGrid.DOWN;
import static com.gathin.tvhome.LibraryGrid.FORWARD;
import static com.gathin.tvhome.LibraryGrid.UP;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LibraryGridTest {
    private static final int[] DIRECTIONS = {UP, DOWN, BACK, FORWARD};

    // Nine cards, five to a row, read left to right:
    //   0 1 2 3 4
    //   5 6 7 8
    @Test public void backAndForwardStayInTheRow() {
        assertEquals(1, LibraryGrid.move(0, 9, 5, FORWARD));
        assertEquals(0, LibraryGrid.move(1, 9, 5, BACK));
        assertEquals(8, LibraryGrid.move(7, 9, 5, FORWARD));
        assertEquals(5, LibraryGrid.move(6, 9, 5, BACK));
    }

    @Test public void theRowEndsHoldBackAndForward() {
        assertEquals(-1, LibraryGrid.move(0, 9, 5, BACK));
        assertEquals(-1, LibraryGrid.move(5, 9, 5, BACK));
        assertEquals(-1, LibraryGrid.move(4, 9, 5, FORWARD));
        assertEquals(-1, LibraryGrid.move(8, 9, 5, FORWARD));
    }

    @Test public void upAndDownKeepTheColumn() {
        assertEquals(5, LibraryGrid.move(0, 9, 5, DOWN));
        assertEquals(8, LibraryGrid.move(3, 9, 5, DOWN));
        assertEquals(0, LibraryGrid.move(5, 9, 5, UP));
        assertEquals(3, LibraryGrid.move(8, 9, 5, UP));
    }

    @Test public void theTopRowHoldsUpAndTheLastRowHoldsDown() {
        assertEquals(-1, LibraryGrid.move(0, 9, 5, UP));
        assertEquals(-1, LibraryGrid.move(4, 9, 5, UP));
        assertEquals(-1, LibraryGrid.move(5, 9, 5, DOWN));
        assertEquals(-1, LibraryGrid.move(8, 9, 5, DOWN));
    }

    @Test public void downIntoTheShorterLastRowLandsOnTheLastCard() {
        // Nothing sits under 4: the last row stops at 8.
        assertEquals(8, LibraryGrid.move(4, 9, 5, DOWN));
    }

    @Test public void aFullLastRowHasNoShortcut() {
        //   0 1 2 3 4
        //   5 6 7 8 9
        assertEquals(9, LibraryGrid.move(4, 10, 5, DOWN));
        assertEquals(-1, LibraryGrid.move(9, 10, 5, DOWN));
        assertEquals(-1, LibraryGrid.move(9, 10, 5, FORWARD));
    }

    @Test public void threeRows() {
        //   0 1 2 3 4
        //   5 6 7 8 9
        //   10 11
        assertEquals(10, LibraryGrid.move(5, 12, 5, DOWN));
        assertEquals(11, LibraryGrid.move(6, 12, 5, DOWN));
        assertEquals(11, LibraryGrid.move(9, 12, 5, DOWN));
        assertEquals(-1, LibraryGrid.move(11, 12, 5, FORWARD));
        assertEquals(6, LibraryGrid.move(11, 12, 5, UP));
    }

    @Test public void fewerCardsThanColumnsIsOneRow() {
        assertEquals(-1, LibraryGrid.move(0, 3, 5, DOWN));
        assertEquals(-1, LibraryGrid.move(2, 3, 5, DOWN));
        assertEquals(-1, LibraryGrid.move(1, 3, 5, UP));
        assertEquals(-1, LibraryGrid.move(2, 3, 5, FORWARD));
    }

    @Test public void oneCardHoldsEveryKey() {
        for (int columns = 1; columns <= 7; columns++) {
            for (int d : DIRECTIONS) assertEquals(-1, LibraryGrid.move(0, 1, columns, d));
        }
    }

    @Test public void anOutOfRangeIndexOrBadInputMovesNowhere() {
        assertEquals(-1, LibraryGrid.move(-1, 9, 5, FORWARD));
        assertEquals(-1, LibraryGrid.move(9, 9, 5, BACK));
        assertEquals(-1, LibraryGrid.move(0, 0, 5, DOWN));
        assertEquals(-1, LibraryGrid.move(0, 9, 0, DOWN));
        assertEquals(-1, LibraryGrid.move(0, 9, 5, 42));
    }

    @Test public void cardsThatFitOneRowMoveLikeTheSingleRowLibrary() {
        // The single row blocked Up and Down, Back on the first card and Forward on the last, and moved one card
        // otherwise.
        for (int count = 1; count <= 12; count++) {
            for (int i = 0; i < count; i++) {
                assertEquals(-1, LibraryGrid.move(i, count, count, UP));
                assertEquals(-1, LibraryGrid.move(i, count, count, DOWN));
                assertEquals(i == 0 ? -1 : i - 1, LibraryGrid.move(i, count, count, BACK));
                assertEquals(i == count - 1 ? -1 : i + 1, LibraryGrid.move(i, count, count, FORWARD));
            }
        }
    }

    @Test public void everyMoveStaysInsideTheGrid() {
        for (int columns = 1; columns <= 7; columns++) {
            for (int count = 0; count <= 40; count++) {
                for (int i = 0; i < count; i++) {
                    for (int d : DIRECTIONS) {
                        int target = LibraryGrid.move(i, count, columns, d);
                        assertTrue(target == -1 || target >= 0 && target < count);
                        assertTrue("a move never lands on the card it left", target != i);
                    }
                }
            }
        }
    }

    @Test public void everyCardIsReachableFromTheFirst() {
        for (int columns = 1; columns <= 7; columns++) {
            for (int count = 1; count <= 40; count++) {
                boolean[] seen = new boolean[count];
                Deque<Integer> queue = new ArrayDeque<>();
                queue.add(0);
                seen[0] = true;
                while (!queue.isEmpty()) {
                    int i = queue.poll();
                    for (int d : DIRECTIONS) {
                        int target = LibraryGrid.move(i, count, columns, d);
                        if (target >= 0 && !seen[target]) {
                            seen[target] = true;
                            queue.add(target);
                        }
                    }
                }
                for (int i = 0; i < count; i++) {
                    assertTrue(columns + " columns, " + count + " cards: card " + i + " unreachable", seen[i]);
                }
            }
        }
    }

    @Test public void backAndForwardNeverChangeTheRow() {
        for (int columns = 1; columns <= 7; columns++) {
            for (int count = 1; count <= 40; count++) {
                for (int i = 0; i < count; i++) {
                    for (int d : new int[]{BACK, FORWARD}) {
                        int target = LibraryGrid.move(i, count, columns, d);
                        if (target >= 0) assertEquals(i / columns, target / columns);
                    }
                }
            }
        }
    }

    @Test public void upAndDownMoveExactlyOneRow() {
        for (int columns = 1; columns <= 7; columns++) {
            for (int count = 1; count <= 40; count++) {
                for (int i = 0; i < count; i++) {
                    int up = LibraryGrid.move(i, count, columns, UP);
                    if (up >= 0) assertEquals(i / columns - 1, up / columns);
                    int down = LibraryGrid.move(i, count, columns, DOWN);
                    if (down >= 0) assertEquals(i / columns + 1, down / columns);
                }
            }
        }
    }

    @Test public void eachMoveIsUndoneByTheOppositeKeyWhereThatKeyMoves() {
        for (int columns = 1; columns <= 7; columns++) {
            for (int count = 1; count <= 40; count++) {
                for (int i = 0; i < count; i++) {
                    int forward = LibraryGrid.move(i, count, columns, FORWARD);
                    if (forward >= 0) assertEquals(i, LibraryGrid.move(forward, count, columns, BACK));
                    int back = LibraryGrid.move(i, count, columns, BACK);
                    if (back >= 0) assertEquals(i, LibraryGrid.move(back, count, columns, FORWARD));
                    int up = LibraryGrid.move(i, count, columns, UP);
                    if (up >= 0) assertEquals(i, LibraryGrid.move(up, count, columns, DOWN));
                }
            }
        }
    }

    @Test public void rowsKeepsToOneToThreeAndToWhatFits() {
        assertEquals(1, LibraryGrid.rows(1, 3));
        assertEquals(2, LibraryGrid.rows(2, 3));
        assertEquals(3, LibraryGrid.rows(3, 3));
        assertEquals(3, LibraryGrid.rows(9, 5));
        assertEquals(2, LibraryGrid.rows(3, 2));
        assertEquals(1, LibraryGrid.rows(0, 3));
        assertEquals(1, LibraryGrid.rows(-4, 3));
        assertEquals(1, LibraryGrid.rows(2, 0));
    }

    @Test public void fitCountsTheRowsThatFit() {
        // 720p and 1080p TVs are both 540dp tall; less the 64dp screen margin, with 280dp for the one-row popup
        // and 174dp for each further row, two rows fit.
        assertEquals(2, LibraryGrid.fit(476, 280, 174));
        assertEquals(3, LibraryGrid.fit(280 + 2 * 174, 280, 174));
        assertEquals(1, LibraryGrid.fit(280 + 173, 280, 174));
        assertEquals(1, LibraryGrid.fit(100, 280, 174));
        assertEquals(1, LibraryGrid.fit(0, 280, 174));
        assertEquals(1, LibraryGrid.fit(-5, 280, 174));
        assertEquals(1, LibraryGrid.fit(900, 280, 0));
    }

    @Test public void fitCountsTheColumnsThatFit() {
        // The 800dp popup less 44dp of padding holds five 144dp cards.
        assertEquals(5, LibraryGrid.fit(756, 0, 144));
        assertEquals(5, LibraryGrid.fit(5 * 144, 0, 144));
        assertEquals(4, LibraryGrid.fit(5 * 144 - 1, 0, 144));
        assertEquals(1, LibraryGrid.fit(100, 0, 144));
        assertEquals(1, LibraryGrid.fit(0, 0, 144));
        assertEquals(1, LibraryGrid.fit(-20, 0, 144));
    }
}
