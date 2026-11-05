import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

//by Nathalie Jean Cornella
class MazeMenuView {
    private static int MENU_WIDTH = MazePanel.MENU_WIDTH;
    private static int MENU_HEIGHT = MazePanel.MENU_HEIGHT;
    private static Color HIGHLIGHT = new Color(213, 126, 48);
    private static Color SELECTED = new Color(245, 210, 100);
    private static Color UNSELECTED = new Color(65, 75, 100);

    private BufferedImage background = readMenuArt("BG (1).png", false);
    private BufferedImage logo = readMenuArt("LOGO.png", true);
    private BufferedImage characterTitle = readMenuArt("character-options.png", true);
    private BufferedImage rulesTitle = readMenuArt("rules.png", true);

    private BufferedImage[] menuLabels = {

        readMenuArt("START.png", true),
        readMenuArt("OPTIONS.png", true),
        readMenuArt("QUIT.png", true)
        
    };

    void draw(Graphics2D g, boolean choosingCharacter, int menuChoice, int characterChoice, MazeCharacter selectedCharacter, boolean muted) {
        
        g.drawImage(background, 0, 0, MENU_WIDTH, MENU_HEIGHT, null);

        if (!choosingCharacter) {

            drawStartMenu(g, menuChoice, selectedCharacter, muted);

        } else {

            drawCharacterMenu(g, characterChoice, muted);
            
        }

    }

    void drawRules(Graphics2D g) {

        g.drawImage(background, 0, 0, MENU_WIDTH, MENU_HEIGHT, null);
        drawMenuArt(g, rulesTitle, 148, 216, 50);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));

        String[] lines = {

            "Memorize, find the exit, and avoid traps!",
            "",
            "Use these powerups to your advantage:",
            "Light - Lights up the maze for 2.5",
            "seconds.",
            "Trace - Reveal the path through the next",
            "maze.",
            "",
            "Survive endless levels and get the",
            "highest score!"

        };

        for (int i = 0; i < lines.length; i++) {

            drawCenteredText(g, lines[i], MENU_WIDTH / 2, 258 + i * 24);

        }

        g.setColor(new Color(217, 130, 46));
        g.fillRoundRect((MENU_WIDTH - 360) / 2, 518, 360, 46, 40, 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        drawCenteredText(g, "Press Enter to Confirm", MENU_WIDTH / 2, 548);

    }

    private void drawStartMenu(Graphics2D g, int menuChoice, MazeCharacter selectedCharacter, boolean muted) {

        drawMenuArt(g, logo, 78, 328, 194);

        drawMenuArt(g, selectedCharacter.sprite(), 296, 68, 66);

        for (int i = 0; i < menuLabels.length; i++) {

            int centerY = 422 + i * 74;

            if (i == menuChoice) {

                g.setColor(HIGHLIGHT);

                g.fillRoundRect((MENU_WIDTH - 200) / 2, centerY - 25, 200, 50, 36, 36);

            }

            drawMenuArt(g, menuLabels[i], centerY - 14, 182, 28);

        }

        drawControls(g, 670, muted);

    }

    private void drawCharacterMenu(Graphics2D g, int characterChoice, boolean muted) {

        drawMenuArt(g, characterTitle, 132, 396, 105);

        MazeCharacter[] characters = MazeCharacter.values();

        for (int i = 0; i < characters.length; i++) {

            int x = 73 + i * 142;
            g.setColor(i == characterChoice ? SELECTED : UNSELECTED);
            g.drawRoundRect(x, 287, 122, 177, 13, 13);

            drawFittedImage(g, characters[i].sprite(), x + 61, 303, 90, 115);

            if (i != characterChoice) {

                g.setColor(new Color(174, 183, 211));

            }

            g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 18));
            drawCenteredText(g, characters[i].displayName(), x + 61, 444);

        }

        drawControls(g, 534, muted);

    }

    private void drawControls(Graphics2D g, int y, boolean muted) {

        g.setColor(Color.WHITE);
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        String controls = "WASD: Controls | Esc: Menu | Enter: Confirm | M: "
                + (muted ? "Unmute" : "Mute");
        drawCenteredText(g, controls, MENU_WIDTH / 2, y);

    }

    private void drawCenteredText(Graphics2D g, String text, int centerX, int y) {

        g.drawString(text, centerX - (g.getFontMetrics().stringWidth(text) + 1) / 2, y);

    }

    private void drawMenuArt(Graphics2D g, BufferedImage art, int y, int maxWidth, int maxHeight) {

        drawFittedImage(g, art, MENU_WIDTH / 2, y, maxWidth, maxHeight);

    }

    private void drawFittedImage(Graphics2D g, BufferedImage art, int centerX, int y, int maxWidth, int maxHeight) {

        double scale = Math.min(maxWidth / (double) art.getWidth(), maxHeight / (double) art.getHeight());

        int width = (int) Math.round(art.getWidth() * scale);
        int height = (int) Math.round(art.getHeight() * scale);

        g.drawImage(art, centerX - (width + 1) / 2, y + (maxHeight - height) / 2, width, height, null);

    }

    private static BufferedImage readMenuArt(String file, boolean trim) {

        try (InputStream stream = ResourceFiles.open("menu/" + file)) {

            BufferedImage art = ImageIO.read(stream);

            if (art == null) {

                throw new IOException("Invalid menu artwork: " + file);

            }

            return trim ? trimTransparentEdges(art) : art;

        } catch (IOException exception) {

            throw new IllegalStateException("Could not load " + file, exception);

        }

    }

    private static BufferedImage trimTransparentEdges(BufferedImage art) {

        int left = art.getWidth();
        int top = art.getHeight();
        int right = -1;
        int bottom = -1;

        for (int y = 0; y < art.getHeight(); y++) {

            for (int x = 0; x < art.getWidth(); x++) {

                if ((art.getRGB(x, y) >>> 24) < 32) {

                    continue;

                }

                left = Math.min(left, x);
                top = Math.min(top, y);
                right = Math.max(right, x);
                bottom = Math.max(bottom, y);

            }

        }

        return right < 0 ? art : art.getSubimage(left, top, right - left + 1, bottom - top + 1);

    }

}

