import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

class GameHudView {
    private static final int HEADER_Y = -8;
    private void centered(Graphics2D g, String text, int centerX, int y) {
        g.drawString(text, centerX - g.getFontMetrics().stringWidth(text) / 2, y);
    }
    void drawPreview(Graphics2D g, MazeSession session) {
        g.setColor(new Color(245, 210, 100));
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
        centered(g, "MEMORIZE THE MAZE: " + session.previewTime, MazePanel.WIDTH / 2, HEADER_Y);
    }

    void drawStats(Graphics2D g, MazeSession session) {
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        centered(g, "Level: " + session.level, 80, HEADER_Y);
        centered(g, "Score: " + session.score, MazePanel.WIDTH - 80, HEADER_Y);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        centered(g, "Best: " + session.highScore, MazePanel.WIDTH - 80, HEADER_Y + 14);

        int minutes = session.remainingSeconds / 60;
        int seconds = session.remainingSeconds % 60;
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        centered(g, String.format("Time: %02d:%02d", minutes, seconds), MazePanel.WIDTH / 2, HEADER_Y);
    }

    void drawInventory(Graphics2D g, MazeSession session) {
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        g.setColor(new Color(255, 155, 176));
        centered(g, "Trace: " + session.inventory.traceOrbs, MazePanel.WIDTH / 4, 421);
        g.setColor(new Color(255, 200, 75));
        centered(g, "Light: " + session.inventory.lightOrbs + " [E]", MazePanel.WIDTH * 3 / 4, 421);
    }

    void drawLostScreen(Graphics2D g, MazeSession session) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, MazePanel.WIDTH, MazePanel.HEIGHT);

        g.setColor(new Color(24, 29, 43));
        g.fillRoundRect(75, 130, 290, 190, 20, 20);
        g.setColor(new Color(225, 70, 55));
        g.drawRoundRect(75, 130, 290, 190, 20, 20);

        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 23));
        if (session.trapDeath) {
            centered(g, "CAUGHT BY A TRAP", MazePanel.WIDTH / 2, 180);
        } else {
            centered(g, "TIME'S UP!", MazePanel.WIDTH / 2, 180);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 18));
        centered(g, "Score: " + session.score, MazePanel.WIDTH / 2, 225);
        centered(g, "Best score: " + session.highScore, MazePanel.WIDTH / 2, 255);
        g.setColor(new Color(245, 210, 100));
        centered(g, "Press R to restart", MazePanel.WIDTH / 2, 295);
    }

    void drawControls(Graphics2D g) {
        g.setColor(new Color(220, 228, 240));
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        centered(g, "Move: WASD / Arrows   R: Restart   M: Mute", MazePanel.WIDTH / 2, 450);
    }
}
