package bundle.installer;

import bundle.App;
import bundle.config.ConfigParseException;
import bundle.config.ConfigParser;
import bundle.config.DownloadConfig;
import bundle.config.InstallerConfig;
import bundle.download.DownloadException;
import bundle.download.DownloadManager;
import bundle.gui.BundleGuiApp;
import bundle.util.OperatingSystem;
import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class BundleInstaller {
    public Path gameDir;
    public String selectedInstall = "";
    public final InstallerConfig installerConfig;
    public final Properties installerProperties;
    public final BundleGuiApp gui;

    public static final SimpleDateFormat LAUNCHER_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public BundleInstaller() {
        InputStream configStream = BundleInstaller.class.getClassLoader().getResourceAsStream("installer_config.json");
        if (configStream != null) {
            InputStreamReader reader = new InputStreamReader(configStream, StandardCharsets.UTF_8);
            JsonObject configObject = new Gson().fromJson(reader, JsonObject.class);
            InstallerConfig cfg;
            try {
                cfg = ConfigParser.parse(configObject);
            } catch (ConfigParseException e) {
                cfg = new InstallerConfig.Builder().build();
                e.printStackTrace();
            }
            this.installerConfig = cfg;
        } else {
            this.installerConfig = new InstallerConfig.Builder().build();
        }
        if (installerConfig.configNames.size() > 0) {
            selectedInstall = installerConfig.configNames.get(0);
        }

        InputStream propertiesStream = App.class.getClassLoader().getResourceAsStream("installer.properties");
        Properties properties = new Properties();
        try {
            properties.load(propertiesStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.installerProperties = properties;

        this.gameDir = OperatingSystem.getCurrent().getMCDir();

        this.gui = new BundleGuiApp(this);
    }

    public void openUI() {
        gui.open();
    }

    public List<DownloadException> install() throws IOException {
        if (this.gameDir == null) {
            return ImmutableList.of(new DownloadException("Selected game directory is null!"));
        }
        DownloadConfig dlConfig = this.installerConfig.configs.get(selectedInstall);

        makeLauncherProfile(selectedInstall, dlConfig);

        if (!Files.exists(gameDir)) {
            return ImmutableList.of(new DownloadException(String.format("Selected game directory '%s' does not exist!", gameDir)));
        }

        Path versions = gameDir.resolve("versions");
        if (!Files.exists(versions)) {
            return ImmutableList.of(new DownloadException(String.format("Selected game directory '%s' does not contain a 'versions' folder!", gameDir)));
        }

        Path version = versions.resolve(dlConfig.id);
        if (!Files.exists(version) || !Files.isDirectory(version)) {
            Files.createDirectory(version);
        }
        JsonObject meta = VersionMetaCreator.createVersionMeta(dlConfig);
        Path metaFile = version.resolve(dlConfig.id+".json");
        BufferedWriter writer = Files.newBufferedWriter(metaFile);
        new Gson().toJson(meta, writer);
        writer.close();

        Path bundleDir = gameDir.resolve(".bundle");
        if (!Files.exists(bundleDir) || !Files.isDirectory(bundleDir)) {
            Files.createDirectory(bundleDir);
        }

        Path installDir = bundleDir.resolve(dlConfig.id);
        if (!Files.exists(installDir) || !Files.isDirectory(installDir)) {
            Files.createDirectory(installDir);
        }

        if (dlConfig.gameDirToCopy != null) {
            copyZipToInstall(dlConfig, installDir);
        }

        return DownloadManager.downloadFilesTo(installDir, dlConfig);
    }

    private void makeLauncherProfile(String name, DownloadConfig download) throws IOException {
        if (this.gameDir == null) {
            throw new IllegalStateException("Tried to make launcher profile when not ready");
        }

        Path profiles = gameDir.resolve("launcher_profiles.json");

        if (!Files.exists(profiles)) {
            throw new IOException("Unable to create launcher profile, game folder does not contain 'launcher_profiles.json'");
        }

        InputStreamReader reader = new InputStreamReader(Files.newInputStream(profiles), StandardCharsets.UTF_8);
        Gson gson = new Gson();
        JsonObject lpObj = gson.fromJson(reader, JsonObject.class);

        if (lpObj.has("profiles") && lpObj.get("profiles").isJsonObject()) {
            JsonObject pfObj = lpObj.getAsJsonObject("profiles");
            JsonObject profile = new JsonObject();
            String date = LAUNCHER_DATE_FORMAT.format(new Date());
            profile.addProperty("created", date);
            profile.addProperty("lastUsed", date);
            profile.addProperty("icon", installerProperties.getProperty("launcher_icon"));
            profile.addProperty("lastVersionId", download.id);
            profile.addProperty("name", name);
            profile.addProperty("type", "custom");
            if (download.separateGameDir) {
                profile.addProperty("gameDir", gameDir.resolve(".bundle").resolve(download.id).toString());
            }
            pfObj.add(installerProperties.getProperty("launcher_profile_id"), profile);
            lpObj.add("profiles", pfObj);
        } else throw new IOException("File 'launcher_profiles.json' is not complete");

        BufferedWriter writer = Files.newBufferedWriter(profiles);
        gson.toJson(lpObj, writer);
        writer.close();
    }

    private void copyZipToInstall(DownloadConfig download, Path installDir) throws IOException {
        InputStream is = App.class.getClassLoader().getResourceAsStream(download.gameDirToCopy);
        if (is != null) {
            ZipInputStream zip = new ZipInputStream(is);

            ZipEntry entry = zip.getNextEntry();
            while (entry != null) {
                Path target = installDir.resolve(entry.getName());
                if (!entry.isDirectory()) {
                    Path parent = target.getParent();
                    if (!Files.exists(parent)) {
                        Files.createDirectories(parent);
                    }
                    BufferedOutputStream w = new BufferedOutputStream(new FileOutputStream(target.toString()));
                    byte[] buf = new byte[2048];
                    int read;
                    while ((read = zip.read(buf)) != -1) {
                        w.write(buf, 0, read);
                    }
                    w.close();
                } else if (!Files.exists(target)) {
                    Files.createDirectories(target);
                }
                zip.closeEntry();
                entry = zip.getNextEntry();
            }

            zip.close();
        }
    }
}
