import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

class MazePathFinder {
    private MazeCell[][] maze;
    private Point goal;

    List<Point> findRoute(MazeCell[][] maze, Point start, Point goal) {
        this.maze = maze;
        this.goal = goal;

        List<Point> route = new ArrayList<Point>();
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        search(start.x, start.y, visited, route);
        return route;
    }

    // Tries each open direction until the exit is found.
    private boolean search(int col, int row, boolean[][] visited,
                           List<Point> route) {
        if (col < 0 || col >= MazeBoard.COLS || row < 0 || row >= MazeBoard.ROWS
                || visited[row][col]) {
            return false;
        }

        visited[row][col] = true;
        route.add(cellCenter(col, row));

        if (col == goal.x && row == goal.y) {
            return true;
        }

        MazeCell cell = maze[row][col];
        if (!cell.top && search(col, row - 1, visited, route)) return true;
        if (!cell.bottom && search(col, row + 1, visited, route)) return true;
        if (!cell.left && search(col - 1, row, visited, route)) return true;
        if (!cell.right && search(col + 1, row, visited, route)) return true;

        route.remove(route.size() - 1);
        return false;
    }

    private Point cellCenter(int col, int row) {
        int x = MazeBoard.OFFSET_X + col * MazeBoard.CELL_SIZE
                + MazeBoard.CELL_SIZE / 2;
        int y = MazeBoard.OFFSET_Y + row * MazeBoard.CELL_SIZE
                + MazeBoard.CELL_SIZE / 2;
        return new Point(x, y);
    }
}
