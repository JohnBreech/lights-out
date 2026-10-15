import javax.swing.JFrame;

//by John Breech Villaverde
public class LightsOut {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Lights Out! - Maze Game");
        MazePanel game = new MazePanel();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(game);
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        game.requestFocusInWindow();
    }
}
