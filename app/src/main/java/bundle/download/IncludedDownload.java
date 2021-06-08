package bundle.download;

import bundle.App;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

// will not work and will go unused because java is jank
public class IncludedDownload extends AbstractDownload {
    public final String fileName;

    public IncludedDownload(String name, String fileName) {
        super(name);
        this.fileName = fileName;
    }

    @Override
    public void downloadTo(Path path) throws DownloadException {
        InputStream jarStream = App.class.getClassLoader().getResourceAsStream(fileName);
        if (jarStream == null) {
            dlEx(String.format("Jar file '%s' not found in installer resources!", fileName));
        }
        try {
            byte[] buf = new byte[jarStream.available()];
            jarStream.read(buf);
            String[] splitFileName = fileName.split("[\\\\/]");
            String file = splitFileName[splitFileName.length - 1].replace(".zip", ".jar");
            Path dest = path.resolve(file);
            Files.write(dest, buf);
        } catch (IOException e) {
            dlEx(e);
        }
    }
}
