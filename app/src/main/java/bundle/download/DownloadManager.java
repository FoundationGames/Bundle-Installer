package bundle.download;

import bundle.config.ConfigParseException;
import bundle.config.DownloadConfig;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DownloadManager {;
    public static final Map<String, TypeParser> DOWNLOAD_TYPES = new HashMap<>();

    public static void setup() {
        DOWNLOAD_TYPES.put("curse", Downloads::curseDownload);
        DOWNLOAD_TYPES.put("direct", Downloads::directDownload);
    }

    public static List<DownloadException> downloadFilesTo(Path path, DownloadConfig config) {
        List<DownloadException> errors = new ArrayList<>();
        for (ImmutableList<AbstractDownload> dlList : config.downloads.values()) {
            boolean success = false;
            for (AbstractDownload download : dlList) {
                if (success) continue;
                try {
                    download.downloadTo(path);
                    success = true;
                } catch (DownloadException e) {
                    errors.add(e);
                }
            }
        }
        return errors;
    }

    @FunctionalInterface
    public interface TypeParser {
        AbstractDownload getDownload(String name, JsonObject data) throws ConfigParseException;
    }
}
