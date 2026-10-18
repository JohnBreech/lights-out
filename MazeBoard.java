import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

//by Rhian Chua
final class MazeBoard {
    static final int ROWS = 15;
    static final int COLS = 15;

    static final int CELL_SIZE = 24;
    static final int OFFSET_X = 40;
    static final int OFFSET_Y = 30;
    private static final int WALL_THICKNESS = 2;

    private static final int TOP = 1;
    private static final int BOTTOM = 2;
    private static final int LEFT = 4;
    private static final int RIGHT = 8;

    /*
     * Each hexadecimal digit stores the four walls for one cell:
     * top, bottom, left, and right. These five layouts are fixed so every
     * playthrough has an intentional difficulty curve rather than a random map.
     */
    private static final String[][] LEVEL_LAYOUTS = {
        {
            "79713133953313B", "D6949E59E69DC59", "49CE63A6312A6AC",
            "CC6395959E5395A", "CC596A6A63ADC69", "CEC611333B5869C",
            "C5A5AC5333A6968", "4ADCDC69533B69C", "C78C62BCC5331AC",
            "C586339CC697A5A", "6AC71B6A69639CD", "796961953295A68",
            "52BC5A6A59C695A", "433AC5B58ECDC69", "6333A63A632A63A"
        },
        {
            "DD5133B51133959", "C6AC539CCE59ECC", "639CCD6A69C69CC",
            "59C62853962B6AC", "EC639C6943B539C", "5239CC5AC53AD68",
            "C59CC6A5A639C5A", "CCCE63963B5A4AD", "6A613B6333AD69C",
            "5338519D53385AC", "497ACC6A69D6A58", "C695A6319629DCC",
            "6BCC795AE53A68C", "53A69C633A719EC", "63332233333A63A"
        },
        {
            "D595B5339531339", "CCC6323BCCDC59E", "CC633395A686A69",
            "C49D59CC53233BC", "CEC4A6A62B59538", "63ACD51953A6A5A",
            "D53ACCC6A795969", "C639CC6339CCC5A", "639C42B53A4A6AD",
            "538C6B5A532333A", "49C639C7A539539", "CCC53A4332B685A",
            "CCC695233959C69", "CC6BCE539ECC6BC", "E63323A723A633A"
        },
        {
            "7397119D533951B", "7969ECC6239CC69", "52B69C63978CCDC",
            "6339CC5969CCCCC", "5338CCC63AC6ACC", "439E6AC733033AC",
            "6963196959E5958", "DC59CCD6A63A6AC", "C6ACC623953395A",
            "43BC6973AC53A69", "C596963338C533A", "CC63A5B59ECCD59",
            "CC539C5A63AC6AC", "4A696863953A538", "633A723B6233A7A"
        },
        {
            "733973311333339", "51B6339E695339E", "C6133BC59623B69",
            "6963396A639595A", "5A71BC5339CCC69", "63949CC59C6A638",
            "53AEC6ACC6331BC", "695329DC6333A5A", "D6A53AC633339CD",
            "439C5949D519CCC", "696AC6AC6ACECCC", "D695A7963385ACC",
            "496A538D53A6968", "CC59496AC59D4BC", "E6A6A6332A6A63A"
        }
    };

    private static final Point[] START_CELLS = {
        new Point(7, 7), new Point(1, 13), new Point(0, 7),
        new Point(7, 14), new Point(14, 0)
    };

    private static final Point[] GOAL_CELLS = {
        new Point(0, 11), new Point(13, 1), new Point(14, 7),
        new Point(7, 0), new Point(0, 14)
    };

    private final Cell[][] maze = new Cell[ROWS][COLS];
    private final Point startCell;
    private final Point goalCell;
    private final Rectangle goal;

    private static final class Cell {
        final boolean top;
        final boolean bottom;
        final boolean left;
        final boolean right;

        Cell(int walls) {
            top = (walls & TOP) != 0;
            bottom = (walls & BOTTOM) != 0;
            left = (walls & LEFT) != 0;
            right = (walls & RIGHT) != 0;
        }
    }

    MazeBoard(int level) {
        String[] layout = LEVEL_LAYOUTS[level - 1];
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                int walls = Character.digit(layout[row].charAt(col), 16);
                maze[row][col] = new Cell(walls);
            }
        }

        startCell = new Point(START_CELLS[level - 1]);
        goalCell = new Point(GOAL_CELLS[level - 1]);
        goal = new Rectangle(cellCenterX(goalCell.x) - 7,
                cellCenterY(goalCell.y) - 7, 14, 14);
    }

    int startX() {
        return cellCenterX(startCell.x);
    }

    int startY() {
        return cellCenterY(startCell.y);
    }

    Point cellCenter(Point cell) {
        return new Point(cellCenterX(cell.x), cellCenterY(cell.y));
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

    List<Point> routeToExit() {
        Point[][] parents = new Point[ROWS][COLS];
        Queue<Point> pending = new ArrayDeque<>();
        pending.add(new Point(startCell));
        parents[startCell.y][startCell.x] = new Point(-1, -1);

        int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        while (!pending.isEmpty()) {
            Point current = pending.remove();
            if (current.equals(goalCell)) {
                break;
            }
            for (int[] direction : directions) {
                int nextRow = current.y + direction[0];
                int nextCol = current.x + direction[1];
                if (nextRow < 0 || nextRow >= ROWS || nextCol < 0 || nextCol >= COLS
                        || parents[nextRow][nextCol] != null
                        || wallBlocks(current.y, current.x, direction[0], direction[1])) {
                    continue;
                }
                parents[nextRow][nextCol] = new Point(current);
                pending.add(new Point(nextCol, nextRow));
            }
        }

        if (parents[goalCell.y][goalCell.x] == null) {
            return new ArrayList<Point>();
        }
        List<Point> route = new ArrayList<>();
        Point cell = new Point(goalCell);
        while (cell.x >= 0) {
            route.add(new Point(cellCenterX(cell.x), cellCenterY(cell.y)));
            cell = parents[cell.y][cell.x];
        }
        Collections.reverse(route);
        return route;
    }

    private boolean wallBlocks(int row, int col, int rowChange, int colChange) {
        Cell cell = maze[row][col];
        if (rowChange < 0) return cell.top;
        if (rowChange > 0) return cell.bottom;
        if (colChange < 0) return cell.left;
        return cell.right;
    }

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
                Cell cell = maze[row][col];
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
