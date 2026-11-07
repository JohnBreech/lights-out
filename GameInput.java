import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.Timer;

class GameInput {
    private MazePanel panel;
    private MazeSession session;
    private boolean upPressed;
    private boolean downPressed;
    private boolean leftPressed;
    private boolean rightPressed;

    GameInput(MazePanel panel, MazeSession session) {
        this.panel = panel;
        this.session = session;

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                pressKey(event.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent event) {
                setMovementKey(event.getKeyCode(), false);
            }
        });

        panel.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent event) {
                clear();
            }
        });

        Timer movementTimer = new Timer(30, event -> movePlayer());
        movementTimer.start();
    }

    private void pressKey(int key) {
        if (panel.gameIsOpen() && isMovementKey(key)) {
            setMovementKey(key, true);
        } else {
            panel.handleKey(key);
        }
    }

    private boolean isMovementKey(int key) {
        return key == KeyEvent.VK_W || key == KeyEvent.VK_UP
                || key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN
                || key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT
                || key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT;
    }

    private void setMovementKey(int key, boolean pressed) {
        if (key == KeyEvent.VK_W || key == KeyEvent.VK_UP) {
            upPressed = pressed;
        } else if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
            downPressed = pressed;
        } else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            leftPressed = pressed;
        } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            rightPressed = pressed;
        }
    }

    private void movePlayer() {
        if (!panel.gameIsOpen()) {
            return;
        }

        if (upPressed) session.move(0, -MazePlayer.STEP);
        if (downPressed) session.move(0, MazePlayer.STEP);
        if (leftPressed) session.move(-MazePlayer.STEP, 0);
        if (rightPressed) session.move(MazePlayer.STEP, 0);
        panel.repaint();
    }

    void clear() {
        upPressed = false;
        downPressed = false;
        leftPressed = false;
        rightPressed = false;
    }
}
