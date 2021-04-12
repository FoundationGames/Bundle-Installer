package bundle.config;

import bundle.download.AbstractDownload;
import bundle.download.DownloadManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class ConfigParser {

    /*
     *              help
     *
     *              this has convinced me that DFU codecs may not be so bad
     */

    public static InstallerConfig parse(JsonObject config) throws ConfigParseException {
        InstallerConfig.Builder iCfg = new InstallerConfig.Builder();
        if (config.get("download_configs").isJsonObject()) {
            JsonObject dcHolder = config.getAsJsonObject("download_configs");
            for (String dcKey : dcHolder.keySet()) {
                if (dcHolder.has(dcKey) && dcHolder.get(dcKey).isJsonObject()) {
                    JsonObject dcObject = dcHolder.getAsJsonObject(dcKey);
                    String dlCfgId = "";
                    if (dcObject.has("id") && dcObject.get("id").isJsonPrimitive() && dcObject.get("id").getAsJsonPrimitive().isString()) {
                        dlCfgId = dcObject.get("id").getAsJsonPrimitive().getAsString();
                    } else cpe("Entry 'id' in download config '%s' was nonexistent or not a string", dcKey);
                    DownloadConfig.Builder dlCfg = new DownloadConfig.Builder(dlCfgId);
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
        return iCfg.build();
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
}
