package bundle.util;

import com.google.common.collect.Sets;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.function.Supplier;

public enum OperatingSystem {
    WINDOWS(() -> home().resolve("AppData").resolve("Roaming").resolve(".minecraft"), "win"),
    MAC(() -> home().resolve("Library").resolve("Application Support").resolve("minecraft"), "mac"),
    LINUX(() -> home().resolve(".minecraft"), "linux", "unix", "nux", "nix"),
    UNKNOWN(OperatingSystem::home);

    private final Set<String> keywords;
    private final Supplier<Path> gameDirGetter;

    OperatingSystem(Supplier<Path> gameDirGetter, String ... keywords) {
        this.keywords = Sets.newHashSet(keywords);
        this.gameDirGetter = gameDirGetter;
    }

    public Path getMCDir() {
        Path dir = gameDirGetter.get();
        if (!Files.exists(dir)) {
            return home();
        }
        return dir;
    }

    private static Path home() {
        return Paths.get(System.getProperty("user.home"));
    }

    public static OperatingSystem getCurrent() {
        String osName = System.getProperty("os.name").toLowerCase();
        for (OperatingSystem os : OperatingSystem.values()) {
            for (String keyword : os.keywords) {
                if (osName.contains(keyword)) {
                    return os;
                }
            }
        }
        return UNKNOWN;
    }
}