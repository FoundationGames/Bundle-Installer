package bundle.config;

import bundle.download.AbstractDownload;
import bundle.download.DownloadManager;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public enum ConfigParser {;
    public static InstallerConfig parse(JsonObject config) throws ConfigParseException {
        InstallerConfig.Builder iCfg = new InstallerConfig.Builder();
        if (config.has("include")) {
            if (config.get("include").isJsonArray()) {
                for (JsonElement include : config.getAsJsonArray("include")) {
                    if (include.isJsonPrimitive() && include.getAsJsonPrimitive().isString()) {
                        String url = include.getAsString();
                        if (url.endsWith(".json")) {
                            try {
                                InputStreamReader reader = new InputStreamReader(new URL(url).openStream(), StandardCharsets.UTF_8);
                                JsonObject remoteConfig = new Gson().fromJson(reader, JsonObject.class);
                                fillBuilder(iCfg, remoteConfig);
                            } catch (IOException e) {
                                cpe(e, "Entry 'include' contained an invalid URL '%s'", url);
                            }
                        } else cpe("Entry 'include' contained a URL '%s' to a non-json file", url);
                    } else cpe("Entry 'include' contained an element which was not a string");
                }
            } else cpe("Entry 'include' in installer config was not an array");
        }
        fillBuilder(iCfg, config);
        return iCfg.build();
    }

    public static void fillBuilder(InstallerConfig.Builder iCfg, JsonObject config) throws ConfigParseException {
        if (config.has("download_configs") && config.get("download_configs").isJsonObject()) {
            JsonObject dcHolder = config.getAsJsonObject("download_configs");
            for (String dcKey : dcHolder.keySet()) {
                if (dcHolder.has(dcKey) && dcHolder.get(dcKey).isJsonObject()) {
                    JsonObject dcObject = dcHolder.getAsJsonObject(dcKey);
                    String dlCfgId = "";
                    String dlCfgLv = "";
                    String dlCfgGv = "";
                    boolean separateGameDir = false;
                    String gameDirToCopy = null;
                    if (dcObject.has("id") && dcObject.get("id").isJsonPrimitive() && dcObject.get("id").getAsJsonPrimitive().isString()) {
                        dlCfgId = dcObject.get("id").getAsString();
                    } else cpe("Entry 'id' in download config '%s' was nonexistent or not a string", dcKey);
                    if (dcObject.has("loader_version") && dcObject.get("loader_version").isJsonPrimitive() && dcObject.get("loader_version").getAsJsonPrimitive().isString()) {
                        dlCfgLv = dcObject.get("loader_version").getAsString();
                    } else cpe("Entry 'loader_version' in download config '%s' was nonexistent or not a string", dcKey);
                    if (dcObject.has("game_version") && dcObject.get("game_version").isJsonPrimitive() && dcObject.get("game_version").getAsJsonPrimitive().isString()) {
                        dlCfgGv = dcObject.get("game_version").getAsString();
                    } else cpe("Entry 'game_version' in download config '%s' was nonexistent or not a string", dcKey);
                    if (dcObject.has("separate_game_dir") && dcObject.get("separate_game_dir").isJsonPrimitive() && dcObject.get("separate_game_dir").getAsJsonPrimitive().isBoolean()) {
                        separateGameDir = dcObject.get("separate_game_dir").getAsBoolean();
                    } else cpe("Entry 'separate_game_dir' in download config '%s' was nonexistent or not a boolean", dcKey);
                    if (dcObject.has("copy_game_dir") && dcObject.get("copy_game_dir").isJsonPrimitive() && dcObject.get("copy_game_dir").getAsJsonPrimitive().isString()) {
                        gameDirToCopy = dcObject.get("copy_game_dir").getAsString();
                    }
                    DownloadConfig.Builder dlCfg = new DownloadConfig.Builder(dlCfgId, dlCfgLv, dlCfgGv, gameDirToCopy, separateGameDir);
                    if (dcObject.has("downloads") && dcObject.get("downloads").isJsonObject()) {
                        JsonObject dlHolder = dcObject.getAsJsonObject("downloads");
                        for (String dlKey : dlHolder.keySet()) {
                            if (dlHolder.get(dlKey).isJsonArray()) {
                                JsonArray dlArray = dlHolder.getAsJsonArray(dlKey);
                                List<AbstractDownload> dlList = new ArrayList<>();
                                for (JsonElement dl : dlArray) {
                                    if (dl.isJsonObject()) {
                                        AbstractDownload download = parseDownload(dcKey, dlKey, dl.getAsJsonObject());
                                        if (download != null) {
                                            dlList.add(download);
                                        }
                                    } else cpe("Array 'downloads' of download '%s' in download config '%s' contained an element which was not an object", dlKey, dcKey);
                                }
                                AbstractDownload[] dlArr = new AbstractDownload[dlList.size()];
                                dlList.toArray(dlArr);
                                dlCfg.with(dlKey, dlArr);
                            } else cpe("Entry '%s' in 'downloads' in download config '%s' was nonexistent or not an array", dlKey, dcKey);
                        }
                    } else cpe("Entry 'downloads' in download config '%s' was nonexistent or not an object", dcKey);
                    iCfg.with(dcKey, dlCfg.build());
                } else cpe("Entry '%s' in 'download_configs' was nonexistent or not an object", dcKey);
            }
        } else cpe("Entry 'download_configs' in the installer config was nonexistent or not an object");
    }

    public static AbstractDownload parseDownload(String dcKey, String dlKey, JsonObject download) throws ConfigParseException {
        if (download.has("type") && download.get("type").isJsonPrimitive() && download.get("type").getAsJsonPrimitive().isString()) {
            String tStr = download.get("type").getAsString();
            DownloadManager.TypeParser type = DownloadManager.DOWNLOAD_TYPES.get(tStr);
            if (type == null) cpe("Entry 'type' in download '%s' in download config '%s' was an invalid download type '%s'", dlKey, dcKey, tStr);
            if (download.has("data") && download.get("data").isJsonObject()) {
                return type.getDownload(dlKey, download.getAsJsonObject("data"));
            } else cpe("Entry 'data' in download '%s' in download config '%s' was nonexistent or not an object", dlKey, dcKey);
        } else cpe("Entry 'type' in download '%s' in download config '%s' was nonexistent or not a string", dlKey, dcKey);
        return null;
    }

    private static void cpe(String msg, Object ... args) throws ConfigParseException {
        throw new ConfigParseException(String.format(msg, args));
    }

    private static void cpe(Throwable cause, String msg, Object ... args) throws ConfigParseException {
        throw new ConfigParseException(String.format(msg, args), cause);
    }
}
