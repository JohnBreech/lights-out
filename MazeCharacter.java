import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.imageio.ImageIO;

enum MazeCharacter {
    GUY("Guy", "player.png"),
    GIRL("Girl", "girl.png"),
    KNIGHT("Knight", "knight.png");

    private final String name;
    private final String file;
    private BufferedImage image;

    MazeCharacter(String name, String file) {
        this.name = name;
        this.file = file;
    }

    String displayName() {
        return name;
    }

    BufferedImage sprite() {
        if (image == null) {
            try (InputStream stream = ResourceFiles.open(file)) {
                image = ImageIO.read(stream);
                if (image == null) {
                    throw new IOException("Invalid character image: " + file);
                }
            } catch (IOException error) {
                throw new IllegalStateException("Could not load " + file, error);
            }
        }
        return image;
    }
}
