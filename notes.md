# My notes

## Chess Board

The chess board is represented as a 2D array of `ChessPiece` objects.
If there is no piece in a given position, it is represented as `null`.

Although `ChessPositions` stores rows and columns with 1,1 being the bottom-left square and 8,8 being the upper-right square,
the array will store `[0][0]` as the upper-left square and `[7][7]` as the bottom-right square.