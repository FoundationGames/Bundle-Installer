package bundle.download;

import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public abstract class AbstractDownload {
    protected final String name;

    public AbstractDownload(String name) {
        this.name = name;
    }

    public abstract void downloadTo(Path path) throws DownloadException;

    @Override
    public String toString() {
        return String.format("%s { name: %s }", this.getClass().getName(), name);
    }

    protected final void dlEx(@Nullable Throwable cause) throws DownloadException {
        if (cause != null) {
            throw new DownloadException(String.format("Failed to download file '%s'!", this.name), cause);
        }
        throw new DownloadException(String.format("Failed to download file '%s'!", this.name));
    }

    protected final void dlEx(String msg) throws DownloadException {
        throw new DownloadException(String.format("Failed to download file '%s', %s", this.name, msg));
    }
}
