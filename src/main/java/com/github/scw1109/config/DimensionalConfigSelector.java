package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

        Set<Map.Entry<String, ConfigValue>> configEntrySet = originalConfig.entrySet();
        for (String pattern : patterns) {
            String patternString = "\"" + DimensionalConfig.DIMENSION_PREFIX + pattern + "\"";
            List<String> patternMatchedKeys = configEntrySet.stream()
                    .filter(e -> e.getKey().contains(patternString))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
            for (String k: patternMatchedKeys) {
                ConfigValue value = originalConfig.getValue(k);
                int start = k.indexOf(patternString);
                String newPath = k.substring(0, start) +
                        k.substring(start + patternString.length(), k.length());
                newPath = newPath.replace("..", ".");
                if (newPath.startsWith(".")) {
                    newPath = newPath.substring(1);
                }

                mergedConfig = mergedConfig.withValue(newPath, value);
            }
        }

        return mergedConfig
                .withFallback(dimensionalConfig.getConfigWithoutDimension())
                .resolve();
    }
}
