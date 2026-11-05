import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

class ScreenDrawer {
    void drawGame(Graphics graphics, int width, int height, MazeView view,
                  MazeSession session, MazeCharacter character) {
        view.drawBackground((Graphics2D) graphics, width, height);
        Graphics2D scaled = scale(graphics, width, height, MazePanel.WIDTH, MazePanel.HEIGHT + 30);
        scaled.translate(0, 30);
        try {
            view.draw(scaled, session, character);
        } finally {
            scaled.dispose();
        }
    }

    void drawMenu(Graphics graphics, int width, int height, MazeMenuView view,
                  int menuChoice, int characterChoice, MazeCharacter character,
                  boolean muted, boolean choosingCharacter) {
        Graphics2D scaled = scale(graphics, width, height,
                MazePanel.MENU_WIDTH, MazePanel.MENU_HEIGHT);
        try {
            view.draw(scaled, choosingCharacter, menuChoice, characterChoice, character, muted);
        } finally {
            scaled.dispose();
        }
    }

    private Graphics2D scale(Graphics graphics, int width, int height,
                             int logicalWidth, int logicalHeight) {
        Graphics2D result = (Graphics2D) graphics.create();
        double factor = Math.min(width / (double) logicalWidth, height / (double) logicalHeight);
        result.translate((width - logicalWidth * factor) / 2,
                (height - logicalHeight * factor) / 2);
        result.scale(factor, factor);
        result.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        return result;
    }
}
