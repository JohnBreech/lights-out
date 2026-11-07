import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

class MazeView {
    private static int ROWS = MazeBoard.ROWS;
    private static int COLS = MazeBoard.COLS;
    private static int CELL_SIZE = MazeBoard.CELL_SIZE;
    private static int OFFSET_X = MazeBoard.OFFSET_X;
    private static int OFFSET_Y = MazeBoard.OFFSET_Y;
    private static int WIDTH = MazePanel.WIDTH;
    private static int HEIGHT = MazePanel.HEIGHT;
    private static int VIEW_RADIUS = 40;


    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private GameHudView hud = new GameHudView();
    private final BufferedImage background = loadArt("BG (1).png");
    private final BufferedImage lightArt = loadArt("light (2).png");
    private final BufferedImage traceArt = loadArt("trace (2).png");
    private final BufferedImage trapArt = loadArt("trap (2).png");

    private static BufferedImage loadArt(String file) {
        try (InputStream stream = ResourceFiles.open("menu/" + file)) {
            BufferedImage art = ImageIO.read(stream);
            if (art == null) {
                throw new IOException("Invalid maze image: " + file + "");
            }
            return art;
        } catch (IOException error) {
            throw new IllegalStateException("Could not load maze image: " + file, error);
        }
    }

    void drawBackground(Graphics2D graphics, int width, int height) {
        graphics.drawImage(background, 0, 0, width, height, null);
        graphics.setColor(new Color(15, 20, 35, 130));
        graphics.fillRect(0, 0, width, height);
    }

    // Draw maze walls
    private void drawMaze(Graphics2D g2, MazeBoard board) {

        g2.setColor(new Color(110, 129, 148));
        g2.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

        for (int row = 0; row < ROWS; row++) {

            for (int col = 0; col < COLS; col++) {

                int x = OFFSET_X + col * CELL_SIZE;
                int y = OFFSET_Y + row * CELL_SIZE;

                if (board.hasTopWall(row, col)) {

                    g2.drawLine(
                            x, y,
                            x + CELL_SIZE, y
                    );
                }

                if (board.hasBottomWall(row, col)) {

                    g2.drawLine(
                            x, y + CELL_SIZE,
                            x + CELL_SIZE, y + CELL_SIZE
                    );
                }

                if (board.hasLeftWall(row, col)) {

                    g2.drawLine(
                            x, y,
                            x, y + CELL_SIZE
                    );
                }

                if (board.hasRightWall(row, col)) {

                    g2.drawLine(
                            x + CELL_SIZE, y,
                            x + CELL_SIZE, y + CELL_SIZE
                    );
                }
            }
        }
    }

    // Draw all game objects
    private void drawGameObjects(Graphics2D g2, MazeSession session, MazeCharacter character) {

        // Background
        g2.setColor(new Color(25, 31, 47, 180));
        g2.fillRect(OFFSET_X, OFFSET_Y, COLS * CELL_SIZE, ROWS * CELL_SIZE);

        // Maze
        drawMaze(g2, session.board);

        List<Point> trace = session.previewTrace;
        if (!trace.isEmpty()) {
            drawTrace(g2, trace);
        }

        for (PowerUp orb : session.pickups) {

            drawOrb(g2, orb);
        }

        // Starting point
        g2.setColor(new Color(40, 150, 70));

        g2.fillOval(session.board.startX() - 7,
                session.board.startY() - 7, 14, 14);

        Rectangle goal = session.board.goal();
        g2.setColor(new Color(100, 235, 255));
        g2.fillOval(goal.x, goal.y, goal.width, goal.height);

        MazePlayer player = session.player;
        if (player.facingDirection == 1) {
            g2.drawImage(character.sprite(), player.x - 7, player.y - 9,
                    14, 18, null);
        } else {
            g2.drawImage(character.sprite(), player.x + 7, player.y - 9,
                    -14, 18, null);
        }
    }

    private void drawTrap(Graphics2D g2, Point trap) {
        drawIcon(g2, trapArt, trap.x, trap.y, 18);
    }

    private void drawTraps(Graphics2D g2, MazeSession session) {
        for (Point trap : session.traps) {
            drawTrap(g2, trap);
        }
    }

    private void drawTrace(Graphics2D graphics, List<Point> route) {
        Path2D path = new Path2D.Double();
        Point start = route.get(0);
        path.moveTo(start.x, start.y);
        for (int i = 1; i < route.size(); i++) {
            Point point = route.get(i);
            path.lineTo(point.x, point.y);
        }
        graphics.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        graphics.setColor(Color.RED);
        graphics.draw(path);
    }

    private void drawOrb(Graphics2D graphics, PowerUp orb) {
        drawIcon(graphics, orb.type == PowerUp.TRACE ? traceArt : lightArt, orb.x, orb.y, 16);
    }

    private void drawIcon(Graphics2D graphics, BufferedImage art, int x, int y, int size) {
        double scale = Math.min(size / (double) art.getWidth(), size / (double) art.getHeight());
        int width = Math.max(1, (int) Math.round(art.getWidth() * scale));
        int height = Math.max(1, (int) Math.round(art.getHeight() * scale));
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            g.drawImage(art, x - width / 2, y - height / 2, width, height, null);
        } finally {
            g.dispose();
        }
    }
    void draw(Graphics2D g2, MazeSession session, MazeCharacter character) {
        // Preview mode: Show the entire maze
        if (session.phase == MazeSession.Phase.PREVIEW) {

            drawGameObjects(g2, session, character);
            drawTraps(g2, session);
            hud.drawPreview(g2, session);

        } else {

            if (session.lightsOn()) {
                drawGameObjects(g2, session, character);
                drawTraps(g2, session);
            } else {
                drawInDarkness(g2, session, character);
            }

            hud.drawStats(g2, session);
        }

        if (session.phase == MazeSession.Phase.LOST) {
            hud.drawLostScreen(g2, session);
        }
        if (session.phase == MazeSession.Phase.PREVIEW
                || session.phase == MazeSession.Phase.PLAYING) {
            hud.drawInventory(g2, session);
        }
        hud.drawControls(g2);
    }

    private void drawInDarkness(Graphics2D g2, MazeSession session, MazeCharacter character) {
        MazePlayer player = session.player;
        Graphics2D mazeGraphics = image.createGraphics();
        mazeGraphics.setComposite(AlphaComposite.Clear);
        mazeGraphics.fillRect(0, 0, WIDTH, HEIGHT);
        mazeGraphics.setComposite(AlphaComposite.SrcOver);

        // Draw the maze and objects
        drawGameObjects(mazeGraphics, session, character);

        mazeGraphics.dispose();

        // Apply dark overlay
        g2.setColor(Color.BLACK);
        g2.fillRect(OFFSET_X, OFFSET_Y, COLS * CELL_SIZE, ROWS * CELL_SIZE);

        // Redraw the visible maze inside the circle
        Shape oldClip = g2.getClip();

        g2.clip(
                new Ellipse2D.Double(
                        player.x - VIEW_RADIUS,
                        player.y - VIEW_RADIUS,
                        VIEW_RADIUS * 2,
                        VIEW_RADIUS * 2
                )
        );

        g2.drawImage(image, 0, 0, null);

        g2.setClip(oldClip);
    }

}


