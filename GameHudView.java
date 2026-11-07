import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

class GameHudView {
    void drawPreview(Graphics2D g, MazeSession session) {
        g.setColor(new Color(245, 210, 100));
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("MEMORIZE THE MAZE: " + session.previewTime, 85, 20);
    }

    void drawStats(Graphics2D g, MazeSession session) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Level: " + session.level, 20, 18);
        g.drawString("Score: " + session.score, 340, 18);

        g.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g.drawString("Best: " + session.highScore, 350, 34);

        int minutes = session.remainingSeconds / 60;
        int seconds = session.remainingSeconds % 60;
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(String.format("Time: %02d:%02d", minutes, seconds), 160, 25);
    }

    void drawInventory(Graphics2D g, MazeSession session) {
        g.setFont(new Font("SansSerif", Font.BOLD, 12));
        g.setColor(new Color(255, 155, 176));
        g.drawString("Trace: " + session.inventory.traceOrbs, 40, 421);
        g.setColor(new Color(255, 200, 75));
        g.drawString("Light: " + session.inventory.lightOrbs + " [E]", 240, 421);
    }

    void drawLostScreen(Graphics2D g, MazeSession session) {
        g.setColor(new Color(0, 0, 0, 190));
        g.fillRect(0, 0, MazePanel.WIDTH, MazePanel.HEIGHT);

        g.setColor(new Color(24, 29, 43));
        g.fillRoundRect(75, 130, 290, 190, 20, 20);
        g.setColor(new Color(225, 70, 55));
        g.drawRoundRect(75, 130, 290, 190, 20, 20);

        g.setFont(new Font("Arial", Font.BOLD, 25));
        if (session.trapDeath) {
            g.drawString("CAUGHT BY A TRAP", 100, 180);
        } else {
            g.drawString("TIME'S UP!", 145, 180);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 18));
        g.drawString("Score: " + session.score, 170, 225);
        g.drawString("Best score: " + session.highScore, 145, 255);
        g.setColor(new Color(245, 210, 100));
        g.drawString("Press R to restart", 145, 295);
    }

    void drawControls(Graphics2D g) {
        g.setColor(new Color(220, 228, 240));
        g.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g.drawString("Move: WASD / Arrows   R: Restart   M: Mute", 85, 450);
    }
}
