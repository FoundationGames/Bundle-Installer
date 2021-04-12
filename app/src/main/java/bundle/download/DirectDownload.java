package bundle.download;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class DirectDownload extends AbstractDownload {
    public final String url;

    public DirectDownload(String name, String url) {
        super(name);
        this.url = url;
    }

    @Override
    public void downloadTo(Path path) throws DownloadException {
        try {
            String[] urlPath = url.split("/");
            InputStream stream = new URL(url).openStream();
            Files.copy(stream, path.resolve(urlPath[urlPath.length - 1]));
        } catch (Throwable e) { dlEx(e); }
    }
}
