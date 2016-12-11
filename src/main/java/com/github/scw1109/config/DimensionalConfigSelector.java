package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author scw1109
 */
public class DimensionalConfigSelector {

    private DimensionalConfig dimensionalConfig;

    private Map<String, String> selectedDimension;

    DimensionalConfigSelector(DimensionalConfig dimensionalConfig) {
        this.dimensionalConfig = dimensionalConfig;
        this.selectedDimension = new HashMap<>(dimensionalConfig.getDimensionsCount());
    }

    public DimensionalConfigSelector set(String dimension, String value) {
        selectedDimension.put(dimension, value);
        return this;
    }

    public Config getConfig() {
        if (!dimensionalConfig.isDimensionAvailable()) {
            return dimensionalConfig.getOriginalConfig();
        }

        List<String> patterns = dimensionalConfig.match(selectedDimension);
        Config originalConfig = dimensionalConfig.getOriginalConfig();
        Config mergedConfig = ConfigFactory.empty();

        for (String pattern : patterns) {
            mergedConfig = mergedConfig.withFallback(originalConfig.getConfig(
                    "\"" + DimensionalConfig.DIMENSION_PREFIX + pattern + "\""));
        }

        return mergedConfig
                .withFallback(dimensionalConfig.getConfigWithoutDimension())
                .resolve();
    }
}
