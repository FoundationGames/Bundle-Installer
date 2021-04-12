package bundle.config;

import bundle.download.AbstractDownload;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class DownloadConfig {
    public final String id;
    public final ImmutableMap<String, ImmutableList<AbstractDownload>> downloads;

    private DownloadConfig(String id, ImmutableMap<String, ImmutableList<AbstractDownload>> downloads) {
        this.id = id;
        this.downloads = downloads;
    }

    @Override
    public String toString() {
        return String.format("%s { id: %s, downloads: %s }", this.getClass().getName(), id, downloads);
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, ImmutableList<AbstractDownload>> downloads = new ImmutableMap.Builder<>();
        private final String id;

        public Builder(String id) {
            this.id = id;
        }

        public Builder with(String id, AbstractDownload ... dls) {
            downloads.put(id, ImmutableList.copyOf(dls));
            return this;
        }

        public DownloadConfig build() {
            return new DownloadConfig(id, downloads.build());
        }
    }
}
