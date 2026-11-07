import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

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
        drawLostScreen(g, MazePanel.WIDTH, MazePanel.HEIGHT, session);
    }

    private final BufferedImage background = load("BG (1).png");
    private final BufferedImage title = trim(load("LOST.png"));

    void drawLostScreen(Graphics2D graphics, int width, int height, MazeSession session) {
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.drawImage(background, 0, 0, width, height, null);
            double scale = Math.min(width / 552.0, height / 690.0);
            g.translate((width - 552 * scale) / 2, (height - 690 * scale) / 2);
            g.scale(scale, scale);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int titleWidth = 350;
            int titleHeight = (int) Math.round(titleWidth * title.getHeight() / (double) title.getWidth());
            g.drawImage(title, (552 - titleWidth) / 2, 237 - titleHeight / 2,
                    titleWidth, titleHeight, null);
            g.setColor(Color.WHITE);
            g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 24));
            centered(g, session.trapDeath ? "Caught by a trap." : "Ran out of time :(", 331);
            centered(g, "Score: " + session.score, 368);
            centered(g, "Best: " + session.highScore, 402);
            g.setColor(new Color(193, 130, 62));
            g.fillRoundRect(100, 459, 352, 44, 40, 40);
            g.setColor(Color.WHITE);
            centered(g, "Press R to play again", 489);
        } finally {
            g.dispose();
        }
    }

    private void centered(Graphics2D g, String text, int y) {
        g.drawString(text, (552 - g.getFontMetrics().stringWidth(text)) / 2, y);
    }

    private static BufferedImage load(String file) {
        try (InputStream stream = ResourceFiles.open("menu/" + file)) {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) throw new IOException("Invalid image: " + file);
            return image;
        } catch (IOException error) {
            throw new IllegalStateException("Could not load loss artwork: " + file, error);
        }
    }

    private static BufferedImage trim(BufferedImage image) {
        int left = image.getWidth(), top = image.getHeight(), right = -1, bottom = -1;
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                if ((image.getRGB(x, y) >>> 24) < 32) continue;
                left = Math.min(left, x);
                top = Math.min(top, y);
                right = Math.max(right, x);
                bottom = Math.max(bottom, y);
            }
        }
        return right < 0 ? image : image.getSubimage(left, top, right - left + 1, bottom - top + 1);
    }

    void drawControls(Graphics2D g) {
        g.setColor(new Color(220, 228, 240));
        g.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 11));
        centered(g, "Move: WASD / Arrows   R: Restart   M: Mute", MazePanel.WIDTH / 2, 450);
    }
}

