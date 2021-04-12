package bundle.download;

public class DownloadException extends Exception {
    public DownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public DownloadException(String message) {
        super(message);
    }
}
