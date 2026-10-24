import java.awt.Point;
import java.util.Random;

class MazeGenerator {
    private Random random = new Random();
    private Point farthestCell;
    private int longestPath;

    Point createMaze(MazeCell[][] maze, Point start) {
        boolean[][] visited = new boolean[maze.length][maze[0].length];
        farthestCell = new Point(start);
        longestPath = 0;
        visit(maze, start.x, start.y, visited, 0);
        return farthestCell;
    }

    // Visits every cell and opens random walls along the way.
    private void visit(MazeCell[][] maze, int col, int row,
                       boolean[][] visited, int distance) {
        visited[row][col] = true;

        if (distance > longestPath) {
            longestPath = distance;
            farthestCell = new Point(col, row);
        }

        int[][] directions = {
            {0, -1}, {0, 1}, {-1, 0}, {1, 0}
        };
        mixDirections(directions);

        for (int[] direction : directions) {
            int nextCol = col + direction[0];
            int nextRow = row + direction[1];
            boolean inside = nextRow >= 0 && nextRow < maze.length
                    && nextCol >= 0 && nextCol < maze[0].length;

            if (inside && !visited[nextRow][nextCol]) {
                openWall(maze, col, row, nextCol, nextRow);
                visit(maze, nextCol, nextRow, visited, distance + 1);
            }
        }
    }

    private void mixDirections(int[][] directions) {
        for (int i = 0; i < directions.length; i++) {
            int randomIndex = random.nextInt(directions.length);
            int[] temp = directions[i];
            directions[i] = directions[randomIndex];
            directions[randomIndex] = temp;
        }
    }

    private void openWall(MazeCell[][] maze, int col, int row,
                          int nextCol, int nextRow) {
        MazeCell first = maze[row][col];
        MazeCell second = maze[nextRow][nextCol];

        if (nextRow < row) {
            first.top = false;
            second.bottom = false;
        } else if (nextRow > row) {
            first.bottom = false;
            second.top = false;
        } else if (nextCol < col) {
            first.left = false;
            second.right = false;
        } else {
            first.right = false;
            second.left = false;
        }
    }
}
