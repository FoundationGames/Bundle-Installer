package bundle.config;

import bundle.download.AbstractDownload;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.jetbrains.annotations.Nullable;

public final class DownloadConfig {
    public final String id;
    public final String loaderVersion;
    public final String gameVersion;
    public final boolean separateGameDir;
    public final @Nullable String gameDirToCopy;
    public final ImmutableMap<String, ImmutableList<AbstractDownload>> downloads;

    private DownloadConfig(String id, String loaderVersion, String gameVersion, boolean separateGameDir, @Nullable String gameDirToCopy, ImmutableMap<String, ImmutableList<AbstractDownload>> downloads) {
        this.id = id;
        this.loaderVersion = loaderVersion;
        this.gameVersion = gameVersion;
        this.separateGameDir = separateGameDir;
        this.gameDirToCopy = gameDirToCopy;
        this.downloads = downloads;
    }

    @Override
    public String toString() {
        return String.format("%s { id: %s, downloads: %s }", this.getClass().getName(), id, downloads);
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, ImmutableList<AbstractDownload>> downloads = new ImmutableMap.Builder<>();
        private final String id;
        private final String loaderVersion;
        private final String gameVersion;
        private final @Nullable String gameDirToCopy;
        private final boolean separateGameDir;

        public Builder(String id, String loaderVersion, String gameVersion, @Nullable String gameDirToCopy, boolean separateGameDir) {
            this.id = id;
            this.loaderVersion = loaderVersion;
            this.gameVersion = gameVersion;
            this.gameDirToCopy = gameDirToCopy;
            this.separateGameDir = separateGameDir;
        }

        public Builder with(String id, AbstractDownload ... dls) {
            downloads.put(id, ImmutableList.copyOf(dls));
            return this;
        }

        public DownloadConfig build() {
            return new DownloadConfig(id, loaderVersion, gameVersion, separateGameDir, gameDirToCopy, downloads.build());
        }
    }
}
