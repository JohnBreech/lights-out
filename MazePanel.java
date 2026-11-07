import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import javax.swing.JPanel;

public class MazePanel extends JPanel {
    static int WIDTH = 440;
    static int HEIGHT = 460;
    static int MENU_WIDTH = 552;
    static int MENU_HEIGHT = 690;

    private enum Screen {
        MENU,
        CHARACTERS,
        GAME
    }

    private GameAudio audio = new GameAudio();
    private MazeSession session = new MazeSession(audio, this::repaint);
    private MazeView mazeView = new MazeView();
    private MazeMenuView menuView = new MazeMenuView();
    private ScreenDrawer screenDrawer = new ScreenDrawer();
    private GameInput input;

    private Screen currentScreen = Screen.MENU;
    private int menuChoice = 0;
    private int characterChoice = 0;
    private MazeCharacter selectedCharacter = MazeCharacter.GUY;

    public MazePanel() {
        setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
        setBackground(new Color(20, 25, 40));
        setFocusable(true);
        input = new GameInput(this, session);
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);

        if (currentScreen == Screen.GAME) {
            screenDrawer.drawGame(graphics, getWidth(), getHeight(),
                    mazeView, session, selectedCharacter);
        } else {
            boolean choosingCharacter = currentScreen == Screen.CHARACTERS;
            screenDrawer.drawMenu(graphics, getWidth(), getHeight(),
                    menuView, menuChoice, characterChoice, selectedCharacter,
                    audio.isMuted(), choosingCharacter);
        }
    }

    void handleKey(int keyCode) {
        if (keyCode == KeyEvent.VK_M) {
            audio.toggle();
        } else if (keyCode == KeyEvent.VK_ESCAPE) {
            session.stop();
            input.clear();
            currentScreen = Screen.MENU;
        } else if (currentScreen == Screen.GAME) {
            handleGameKey(keyCode);
        } else {
            handleMenuKey(keyCode);
        }

        repaint();
    }

    // Handles keys that are used during the game.
    private void handleGameKey(int keyCode) {
        if (keyCode == KeyEvent.VK_E) {
            session.useLightOrb();
        } else if (keyCode == KeyEvent.VK_R) {
            session.start();
            requestFocusInWindow();
        }
    }

    boolean gameIsOpen() {
        return currentScreen == Screen.GAME;
    }

    // Handles controls while the player is in a menu.
    // updated to allow arrow keys.
    private void handleMenuKey(int keyCode) {
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_LEFT) {
            moveMenuSelection(-1);
        } else if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_RIGHT) {
            moveMenuSelection(1);
        } else if (keyCode == KeyEvent.VK_ENTER) {
            selectMenuOption();
        }
    }

    private void moveMenuSelection(int direction) {
        audio.cue("change");

        if (currentScreen == Screen.MENU) {
            menuChoice = menuChoice + direction;
            if (menuChoice < 0) {
                menuChoice = 2;
            } else if (menuChoice > 2) {
                menuChoice = 0;
            }
        } else if (currentScreen == Screen.CHARACTERS) {
            characterChoice = characterChoice + direction;
            int numberOfCharacters = MazeCharacter.values().length;

            if (characterChoice < 0) {
                characterChoice = numberOfCharacters - 1;
            } else if (characterChoice >= numberOfCharacters) {
                characterChoice = 0;
            }
        }
    }

    private void selectMenuOption() {
        audio.cue("confirm");

        if (currentScreen == Screen.CHARACTERS) {
            selectedCharacter = MazeCharacter.values()[characterChoice];
            currentScreen = Screen.MENU;
            return;
        }

        if (menuChoice == 0) {
            currentScreen = Screen.GAME;
            session.start();
        } else if (menuChoice == 1) {
            characterChoice = selectedCharacter.ordinal();
            currentScreen = Screen.CHARACTERS;
        } else if (menuChoice == 2) {
            closeWindow();
        }
    }

    private void closeWindow() {
        System.exit(0);
    }

}
