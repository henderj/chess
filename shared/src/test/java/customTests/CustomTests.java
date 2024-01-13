package customTests;

import chess.*;
import org.junit.jupiter.api.*;

import static passoffTests.TestFactory.*;

public class CustomTests {
    @Test
    public void bishopMoveUntilEdge() {
        validateMoves("""
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | |B| | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        | | | | | | | | |
                        """,
                startPosition(5, 4),
                endPositions(new int[][]{
                        {6, 5}, {7, 6}, {8, 7},
                        {4, 5}, {3, 6}, {2, 7}, {1, 8},
                        {4, 3}, {3, 2}, {2, 1},
                        {6, 3}, {7, 2}, {8, 1},
                })
        );
    }

}
