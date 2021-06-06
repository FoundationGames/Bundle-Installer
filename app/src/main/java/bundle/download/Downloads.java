package bundle.download;

import bundle.config.ConfigParseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public enum Downloads {;
    public static AbstractDownload curseDownload(String name, JsonObject data) throws ConfigParseException {
        JsonElement projectElement = data.get("project");
        JsonElement fileElement = data.get("file");

        int projectId;
        int fileId;

        if (projectElement.isJsonPrimitive() && projectElement.getAsJsonPrimitive().isNumber()) {
            projectId = projectElement.getAsInt();
        } else throw new ConfigParseException("Field 'project' must be an integer!");

        if (fileElement.isJsonPrimitive() && fileElement.getAsJsonPrimitive().isNumber()) {
            fileId = fileElement.getAsInt();
        } else throw new ConfigParseException("Field 'file' must be an integer!");

        return new CurseDownload(name, projectId, fileId);
    }

    public static AbstractDownload modrinthDownload(String name, JsonObject data) throws ConfigParseException {
        JsonElement id = data.get("version_id");
        JsonElement sha1 = data.get("sha1");

        String versionId;
        String sha1Hash;

        if (id.isJsonPrimitive() && id.getAsJsonPrimitive().isString()) {
            versionId = id.getAsString();
        } else throw new ConfigParseException("Field 'version_id' must be a string!");

        if (sha1.isJsonPrimitive() && sha1.getAsJsonPrimitive().isString()) {
            sha1Hash = sha1.getAsString();
        } else throw new ConfigParseException("Field 'sha1' must be a string!");

        return new ModrinthDownload(name, versionId, sha1Hash);
    }

    public static AbstractDownload directDownload(String name, JsonObject data) throws ConfigParseException {
        JsonElement urlElement = data.get("url");

        String url;

        if (urlElement.isJsonPrimitive() && urlElement.getAsJsonPrimitive().isString()) {
            url = urlElement.getAsString();
        } else throw new ConfigParseException("Field 'url' must be a string!");

        return new DirectDownload(name, url);
    }
}
