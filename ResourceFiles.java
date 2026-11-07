import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

final class ResourceFiles {
    private ResourceFiles() { }

    static InputStream open(String name) throws IOException {
        for (String prefix : new String[] {"/", "/resources/"}) {
            InputStream stream = ResourceFiles.class.getResourceAsStream(prefix + name);
            if (stream != null) {
                return stream;
            }
        }
        Path path = Path.of("resources", name);
        if (Files.isRegularFile(path)) {
            return Files.newInputStream(path);
        }
        throw new IOException("Missing resource: " + name);
    }
}
