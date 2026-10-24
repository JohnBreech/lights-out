import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.util.List;
import java.awt.image.BufferedImage;

class MazeView {
    private static int ROWS = MazeBoard.ROWS;
    private static int COLS = MazeBoard.COLS;
    private static int CELL_SIZE = MazeBoard.CELL_SIZE;
    private static int OFFSET_X = MazeBoard.OFFSET_X;
    private static int OFFSET_Y = MazeBoard.OFFSET_Y;
    private static int WIDTH = MazePanel.WIDTH;
    private static int HEIGHT = MazePanel.HEIGHT;
    private static int VIEW_RADIUS = 40;
    private static Color TRACE_COLOR = new Color(255, 72, 120);
    private static Color LIGHT_COLOR = new Color(255, 200, 75);
    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
    private GameHudView hud = new GameHudView();

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
        g2.setColor(new Color(25, 31, 47));
        g2.fillRect(0, 0, WIDTH, HEIGHT);
        g2.setColor(new Color(45, 52, 63));
        g2.fillRect(OFFSET_X, OFFSET_Y, COLS * CELL_SIZE, ROWS * CELL_SIZE);

        // Maze
        drawMaze(g2, session.board);

        List<Point> trace = session.previewTrace;
        if (!trace.isEmpty()) {
            drawTrace(g2, trace);
        }

        for (PowerUp orb : session.pickups) {
            Color color = orb.type == PowerUp.TRACE ? TRACE_COLOR : LIGHT_COLOR;
            drawOrb(g2, orb, color);
        }

        // Starting point
        g2.setColor(new Color(40, 150, 70));

        g2.fillOval(session.board.startX() - 7,
                session.board.startY() - 7, 14, 14);

        Rectangle goal = session.board.goal();
        // Exit
        g2.setColor(new Color(100, 235, 255));

        g2.fillOval(
                goal.x,
                goal.y,
                goal.width,
                goal.height
        );

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
        g2.setColor(new Color(70, 20, 25));
        g2.fillOval(trap.x - 8, trap.y + 3, 16, 6);

        g2.setColor(new Color(225, 70, 55));
        int[] xPoints = {
            trap.x - 7, trap.x - 4, trap.x - 1,
            trap.x + 2, trap.x + 5, trap.x + 8
        };
        int[] yPoints = {
            trap.y + 5, trap.y - 6, trap.y + 5,
            trap.y - 6, trap.y + 5, trap.y + 5
        };
        g2.fillPolygon(xPoints, yPoints, 6);

        g2.setColor(new Color(255, 170, 80));
        g2.drawPolygon(xPoints, yPoints, 6);
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

    private void drawOrb(Graphics2D graphics, PowerUp orb, Color color) {
        graphics.setColor(color);
        graphics.fillOval(orb.x - 5, orb.y - 5, 10, 10);
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

        // Draw the maze and objects
        drawGameObjects(mazeGraphics, session, character);

        mazeGraphics.dispose();

        // Apply dark overlay
        g2.setColor(Color.BLACK);
        g2.fillRect(0, 0, WIDTH, HEIGHT);

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
