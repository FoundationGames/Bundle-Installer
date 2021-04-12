package bundle.config;

import com.google.common.collect.ImmutableMap;

public final class InstallerConfig {
    public final ImmutableMap<String, DownloadConfig> configs;

    private InstallerConfig(ImmutableMap<String, DownloadConfig> configs) {
        this.configs = configs;
    }

    @Override
    public String toString() {
        return String.format("%s { configs: %s }", this.getClass().getName(), configs);
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, DownloadConfig> configs = new ImmutableMap.Builder<>();

        public Builder with(String id, DownloadConfig download) {
            configs.put(id, download);
            return this;
        }

        public InstallerConfig build() {
            return new InstallerConfig(configs.build());
        }
    }
}
