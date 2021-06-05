package bundle.download;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO: ACTUALLY MAKE WORK, CONFIG PARSING DOES NOT YET EXIST FOR THIS
public class ModrinthDownload extends AbstractDownload {
    private static final String URL_BASE = "https://api.modrinth.com/api/v1/version/";

    public final String id, sha1;

    public ModrinthDownload(String name, String id, String sha1) {
        super(name);
        this.id = id;
        this.sha1 = sha1;
    }

    @Override
    public void downloadTo(Path path) throws DownloadException {
        try {
            URL url = new URL(URL_BASE + id);
            HttpURLConnection cnn = (HttpURLConnection)url.openConnection();
            cnn.setRequestMethod("GET");
            cnn.setInstanceFollowRedirects(true);
            InputStreamReader reader = new InputStreamReader(cnn.getInputStream(), StandardCharsets.UTF_8);
            JsonObject data = new Gson().fromJson(reader, JsonObject.class);
            if (data.has("files") && data.get("files").isJsonArray()) {
                for (JsonElement e : data.getAsJsonArray("files")) {
                    if (e.isJsonObject()) {
                        JsonObject file = e.getAsJsonObject();
                        if (file.has("hashes") && file.get("hashes").isJsonObject()) {
                            JsonObject hashes = file.get("hashes").getAsJsonObject();
                            if (hashes.has("sha1") && hashes.get("sha1").isJsonPrimitive() && hashes.getAsJsonPrimitive("sha1").isString()) {
                                String hash = hashes.getAsJsonPrimitive("sha1").getAsString();
                                if (hash.equals(this.sha1)) {
                                    if (file.has("url") && file.get("url").isJsonPrimitive() && file.getAsJsonPrimitive("url").isString()) {
                                        String fileUrl = file.getAsJsonPrimitive("url").getAsString();
                                        String[] urlPath = fileUrl.split("/");
                                        InputStream stream = new URL(fileUrl).openStream();
                                        Files.copy(stream, path.resolve(urlPath[urlPath.length - 1]));
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
                dlEx(String.format("Requested version '%s' from Modrinth contains no matching or existing files!", id));
            } else dlEx(String.format("Requested version '%s' from Modrinth contains no files!", id));
        } catch (Exception e) {
            dlEx(e);
        }
    }
}
