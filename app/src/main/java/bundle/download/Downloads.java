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

    public static AbstractDownload directDownload(String name, JsonObject data) throws ConfigParseException {
        JsonElement urlElement = data.get("url");

        String url;

        if (urlElement.isJsonPrimitive() && urlElement.getAsJsonPrimitive().isString()) {
            url = urlElement.getAsString();
        } else throw new ConfigParseException("Field 'url' must be a string!");

        return new DirectDownload(name, url);
    }
}
