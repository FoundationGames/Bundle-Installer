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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

public final class BundleInstaller {
    public Path gameDir;
    public String selectedInstall = "";
    public final InstallerConfig installerConfig;
    public final Properties installerProperties;
    public final BundleGuiApp gui;

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

        if (!Files.exists(gameDir)) {
            return ImmutableList.of(new DownloadException(String.format("Selected game directory '%s' does not exist!", gameDir)));
        }

        Path bundleDir = gameDir.resolve(".bundle");
        if (!Files.exists(bundleDir) || !Files.isDirectory(bundleDir)) {
            Files.createDirectory(bundleDir);
        }

        return DownloadManager.downloadFilesTo(bundleDir, dlConfig);
    }
}
