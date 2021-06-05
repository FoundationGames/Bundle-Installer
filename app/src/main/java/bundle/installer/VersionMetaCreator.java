package bundle.installer;

import bundle.config.DownloadConfig;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public enum VersionMetaCreator {;
    public static final String META_URL = "https://meta.fabricmc.net/v2/versions/loader/%s/%s/profile/json";

    public static JsonObject createVersionMeta(DownloadConfig config) {
        try {
            InputStreamReader reader = new InputStreamReader(new URL(String.format(META_URL, config.gameVersion, config.loaderVersion)).openStream(), StandardCharsets.UTF_8);
            JsonObject data = new Gson().fromJson(reader, JsonObject.class);
            data.addProperty("id", config.id);
            return data;
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new JsonObject();
    }
}
