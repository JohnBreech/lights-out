import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//by Rhian Chua
class MazeBoard {
    static int ROWS = 15;
    static int COLS = 15;

    static int CELL_SIZE = 24;
    static int OFFSET_X = 40;
    static int OFFSET_Y = 30;
    private static int WALL_THICKNESS = 2;

    private static Random RANDOM = new Random();

    private MazeCell[][] maze = new MazeCell[ROWS][COLS];
    private Point startCell;
    private Point goalCell;
    private Rectangle goal;
    private MazePathFinder pathFinder = new MazePathFinder();

    MazeBoard(int level) {
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                maze[row][col] = new MazeCell();
            }
        }

        startCell = new Point(RANDOM.nextInt(COLS), RANDOM.nextInt(ROWS));
        MazeGenerator generator = new MazeGenerator();
        goalCell = generator.createMaze(maze, startCell);

        goal = new Rectangle(cellCenterX(goalCell.x) - 7,
                cellCenterY(goalCell.y) - 7, 14, 14);
    }

    int startX() {
        return cellCenterX(startCell.x);
    }

    int startY() {
        return cellCenterY(startCell.y);
    }

    private static int cellCenterX(int col) {
        return OFFSET_X + col * CELL_SIZE + CELL_SIZE / 2;
    }

    private static int cellCenterY(int row) {
        return OFFSET_Y + row * CELL_SIZE + CELL_SIZE / 2;
    }

    Rectangle goal() {
        return goal;
    }

    boolean reachedGoal(Rectangle player) {
        return player.intersects(goal);
    }

    boolean hasTopWall(int row, int col) {
        return maze[row][col].top;
    }

    boolean hasBottomWall(int row, int col) {
        return maze[row][col].bottom;
    }

    boolean hasLeftWall(int row, int col) {
        return maze[row][col].left;
    }

    boolean hasRightWall(int row, int col) {
        return maze[row][col].right;
    }

    // Builds the safe path from the starting point to the exit.
    List<Point> routeToExit() {
        return pathFinder.findRoute(maze, startCell, goalCell);
    }

    // Puts traps only on cells that are not part of the safe path.
    List<Point> makeTrapPositions(int amount) {
        List<Point> safeRoute = routeToExit();
        List<Point> choices = new ArrayList<Point>();

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Point position = new Point(cellCenterX(col), cellCenterY(row));
                if (!safeRoute.contains(position)) {
                    choices.add(position);
                }
            }
        }

        Collections.shuffle(choices);
        List<Point> traps = new ArrayList<Point>();
        int trapCount = Math.min(amount, choices.size());
        for (int i = 0; i < trapCount; i++) {
            traps.add(choices.get(i));
        }
        return traps;
    }

    // Chooses a random free cell for an orb.
    Point randomItemPosition(List<Point> blocked) {
        List<Point> choices = new ArrayList<Point>();
        Point startPosition = new Point(startX(), startY());
        Point goalPosition = new Point(cellCenterX(goalCell.x), cellCenterY(goalCell.y));

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                Point position = new Point(cellCenterX(col), cellCenterY(row));
                if (!position.equals(startPosition) && !position.equals(goalPosition)
                        && !blocked.contains(position)) {
                    choices.add(position);
                }
            }
        }

        return choices.get(RANDOM.nextInt(choices.size()));
    }

    // Returns false when the player would touch a wall.
    boolean canOccupy(Rectangle next) {
        if (next.x < OFFSET_X
                || next.y < OFFSET_Y
                || next.x + next.width > OFFSET_X + COLS * CELL_SIZE
                || next.y + next.height > OFFSET_Y + ROWS * CELL_SIZE) {
            return false;
        }

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int x = OFFSET_X + col * CELL_SIZE;
                int y = OFFSET_Y + row * CELL_SIZE;
                MazeCell cell = maze[row][col];
                if ((cell.top && next.intersects(x, y, CELL_SIZE, WALL_THICKNESS))
                        || (cell.bottom && next.intersects(
                                x, y + CELL_SIZE - WALL_THICKNESS, CELL_SIZE, WALL_THICKNESS))
                        || (cell.left && next.intersects(x, y, WALL_THICKNESS, CELL_SIZE))
                        || (cell.right && next.intersects(
                                x + CELL_SIZE - WALL_THICKNESS, y, WALL_THICKNESS, CELL_SIZE))) {
                    return false;
                }
            }
        }
        return true;
    }
}
