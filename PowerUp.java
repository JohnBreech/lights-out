import java.awt.Rectangle;

class PowerUp {
    static int LIGHT = 1;
    static int TRACE = 2;

    int x;
    int y;
    int type;

    PowerUp(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    boolean touches(Rectangle player) {
        return player.intersects(x - 5, y - 5, 10, 10);
    }
}
