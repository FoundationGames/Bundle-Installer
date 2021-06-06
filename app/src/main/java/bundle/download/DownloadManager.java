package bundle.download;

import bundle.config.ConfigParseException;
import bundle.config.DownloadConfig;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum DownloadManager {;
    public static final Map<String, TypeParser> DOWNLOAD_TYPES = new HashMap<>();

    public static void setup() {
        DOWNLOAD_TYPES.put("curse", Downloads::curseDownload);
        DOWNLOAD_TYPES.put("modrinth", Downloads::modrinthDownload);
        DOWNLOAD_TYPES.put("direct", Downloads::directDownload);
    }

    public static List<DownloadException> downloadFilesTo(Path path, DownloadConfig config) throws IOException {
        List<DownloadException> errors = new ArrayList<>();
        Path modFolder = path.resolve(config.id);
        if (!Files.exists(modFolder) || !Files.isDirectory(modFolder)) {
            Files.createDirectory(modFolder);
        }
        modFolder = modFolder.resolve("mods");
        if (!Files.exists(modFolder) || !Files.isDirectory(modFolder)) {
            Files.createDirectory(modFolder);
        }
        for (ImmutableList<AbstractDownload> dlList : config.downloads.values()) {
            boolean success = false;
            for (AbstractDownload download : dlList) {
                if (success) continue;
                try {
                    download.downloadTo(modFolder);
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
