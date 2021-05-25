package bundle.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public final class InstallerConfig {
    public final ImmutableMap<String, DownloadConfig> configs;
    public final ImmutableList<String> configNames;

    private InstallerConfig(ImmutableMap<String, DownloadConfig> configs, ImmutableList<String> configNames) {
        this.configs = configs;
        this.configNames = configNames;
    }

    @Override
    public String toString() {
        return String.format("%s { configs: %s }", this.getClass().getName(), configs);
    }

    public static class Builder {
        private final ImmutableMap.Builder<String, DownloadConfig> configs = new ImmutableMap.Builder<>();
        private final ImmutableList.Builder<String> configNames = new ImmutableList.Builder<>();

        public Builder with(String id, DownloadConfig download) {
            configs.put(id, download);
            configNames.add(id);
            return this;
        }

        public InstallerConfig build() {
            return new InstallerConfig(configs.build(), configNames.build());
        }
    }
}
