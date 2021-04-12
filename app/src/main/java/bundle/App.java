/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package bundle;

import bundle.config.ConfigParseException;
import bundle.config.ConfigParser;
import bundle.config.DownloadConfig;
import bundle.config.InstallerConfig;
import bundle.download.DownloadException;
import bundle.download.DownloadManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;

public class App {
    public static final InstallerConfig INSTALLER_CONFIG;
    public static final Properties INSTALLER_PROPERTIES;

    public static void main(String[] args) {
        String cfg = INSTALLER_PROPERTIES.getProperty("dev_download_config");

        DownloadConfig dlConfig = INSTALLER_CONFIG.configs.get(cfg);

        String dir = INSTALLER_PROPERTIES.getProperty("dev_download_dir");
        Path dlPath = Paths.get(dir);

        if (!Files.exists(dlPath)) {
            System.out.println(String.format("Path '%s' specified as 'dev_download_dir' in 'installer.properties' does not exist!", dir));
            return;
        }

        List<DownloadException> exceptions = DownloadManager.downloadFilesTo(dlPath, dlConfig);

        for (DownloadException ex : exceptions) {
            ex.printStackTrace();
        }
    }

    static {
        DownloadManager.setup();

        InputStream installerConfig = App.class.getClassLoader().getResourceAsStream("installer_config.json");
        if (installerConfig != null) {
            InputStreamReader reader = new InputStreamReader(installerConfig, StandardCharsets.UTF_8);
            JsonObject configObject = new Gson().fromJson(reader, JsonObject.class);
            InstallerConfig cfg;
            try {
                cfg = ConfigParser.parse(configObject);
            } catch (ConfigParseException e) {
                cfg = new InstallerConfig.Builder().build();
                e.printStackTrace();
            }
            INSTALLER_CONFIG = cfg;
        } else INSTALLER_CONFIG = new InstallerConfig.Builder().build();

        InputStream propertiesStream = App.class.getClassLoader().getResourceAsStream("installer.properties");
        Properties properties = new Properties();
        try {
            properties.load(propertiesStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        INSTALLER_PROPERTIES = properties;
    }
}