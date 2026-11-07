import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.Timer;

class MazeSession {
    enum Phase { PREVIEW, PLAYING, LOST }

    private static int MAX_TRAPS = 40;

    private GameAudio audio;
    MazePlayer player = new MazePlayer();
    private Timer countdown;
    private Timer lights;
    MazeBoard board;
    Phase phase;
    int level;
    int score;
    int highScore;
    int previewTime;
    int remainingSeconds;
    boolean trapDeath;
    PowerUpInventory inventory = new PowerUpInventory();
    List<PowerUp> pickups = new ArrayList<PowerUp>();
    List<Point> traps = new ArrayList<Point>();
    List<Point> previewTrace = new ArrayList<Point>();

    MazeSession(GameAudio audio, Runnable repaint) {
        this.audio = audio;
        lights = new Timer(1500, event -> repaint.run());
        lights.setRepeats(false);
        countdown = new Timer(1000, event -> {
            tick();
            repaint.run();
        });
    }

    // Starts a new run and resets the score.
    void start() {
        level = 1;
        score = 0;
        trapDeath = false;
        inventory.clear();
        startLevel();
    }

    // Stops all game timers.
    void stop() {
        countdown.stop();
        lights.stop();
    }

    // Creates the next random maze, traps, and orbs.
    private void startLevel() {
        lights.stop();
        board = new MazeBoard(level);
        previewTrace = new ArrayList<Point>();
        if (inventory.useTraceOrb()) {
            previewTrace = board.routeToExit();
        }

        pickups.clear();
        traps.clear();
        int trapCount = Math.min(2 + level / 2, MAX_TRAPS);
        traps.addAll(board.makeTrapPositions(trapCount));

        List<Point> blocked = new ArrayList<Point>(traps);
        Point lightPosition = board.randomItemPosition(blocked);
        pickups.add(new PowerUp(lightPosition.x, lightPosition.y, PowerUp.LIGHT));
        blocked.add(lightPosition);
        Point tracePosition = board.randomItemPosition(blocked);
        pickups.add(new PowerUp(tracePosition.x, tracePosition.y, PowerUp.TRACE));

        player.resetPosition(board);
        phase = Phase.PREVIEW;
        previewTime = Math.max(3, 10 - level / 3);
        countdown.restart();
    }

    // Runs once every second for the preview and game timer.
    private void tick() {
        if (phase == Phase.PREVIEW) {
            if (--previewTime == 0) {
                phase = Phase.PLAYING;
                previewTrace = new ArrayList<Point>();
                remainingSeconds = Math.max(60, 300 - level * 5);
                audio.cue("ready");
                countdown.restart();
            }
        } else if (phase == Phase.PLAYING && --remainingSeconds == 0) {
            phase = Phase.LOST;
            highScore = Math.max(highScore, score);
            audio.cue("lose");
            stop();
        }
    }

    // Moves the player and checks pickups, traps, and the exit.
    void move(int dx, int dy) {
        if (phase != Phase.PLAYING || !player.move(dx, dy, board)) {
            return;
        }
        audio.step();
        Rectangle playerBounds = player.bounds();
        Iterator<PowerUp> iterator = pickups.iterator();
        while (iterator.hasNext()) {
            PowerUp pickup = iterator.next();
            if (pickup.touches(playerBounds) && inventory.collect(pickup)) {
                iterator.remove();
                audio.cue("orb");
            }
        }

        for (Point trap : traps) {
            Rectangle trapArea = new Rectangle(trap.x - 7, trap.y - 7, 14, 14);
            if (playerBounds.intersects(trapArea)) {
                trapDeath = true;
                phase = Phase.LOST;
                highScore = Math.max(highScore, score);
                audio.cue("lose");
                stop();
                return;
            }
        }

        if (board.reachedGoal(playerBounds)) {
            audio.cue("level");
            score += 100;
            highScore = Math.max(highScore, score);
            level++;
            startLevel();
        }
    }

    // Uses one light orb if the player has one.
    void useLightOrb() {
        if (phase == Phase.PLAYING && !lightsOn() && inventory.useLightOrb()) {
            lights.restart();
            audio.cue("orb");
        }
    }

    boolean lightsOn() {
        return lights.isRunning();
    }

}
