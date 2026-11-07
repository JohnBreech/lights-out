import java.awt.Rectangle;


class MazePlayer {
    private static int SIZE = 14;
    static int STEP = 4;

    int x;
    int y;
    int facingDirection = 1;

    void resetPosition(MazeBoard board) {
        x = board.startX();
        y = board.startY();
    }

    boolean move(int dx, int dy, MazeBoard board) {
        int nextX = x + dx;
        int nextY = y + dy;
        if (!board.canOccupy(boundsAt(nextX, nextY))) {
            return false;
        }
        x = nextX;
        y = nextY;
        if (dx < 0) {
            facingDirection = -1;
        } else if (dx > 0) {
            facingDirection = 1;
        }
        return true;
    }

    Rectangle bounds() {
        return boundsAt(x, y);
    }

    private Rectangle boundsAt(int centerX, int centerY) {
        return new Rectangle(centerX - SIZE / 2, centerY - SIZE / 2, SIZE, SIZE);
    }

}
