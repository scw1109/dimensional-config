package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigObject;
import com.typesafe.config.ConfigValue;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author scw1109
 */
public class DimensionalConfig {

    static final String DIMENSIONS = "dimensions";
    static final String DIMENSION_PREFIX = "dimension:";

    private final Config originalConfig;
    private final boolean dimensionAvailable;

    private DefinedDimensions definedDimensions;
    private List<DimensionItem> dimensionItems;
    private Config configWithoutDimension;

    private DimensionalConfig(Config originalConfig) {
        this.originalConfig = originalConfig;
        this.dimensionAvailable = originalConfig.hasPath(DIMENSIONS);

        if (dimensionAvailable) {
            Config dimensionConfig = originalConfig.getConfig(DIMENSIONS);
            definedDimensions = ConfigBeanFactory.create(dimensionConfig, DefinedDimensions.class);

            ConfigObject configRoot = originalConfig.root();

            Set<Map.Entry<String, ConfigValue>> entry = originalConfig.entrySet();
            List<String> keysWithDimension = entry.stream()
                    .filter(e -> e.getKey().contains(DIMENSION_PREFIX))
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            this.dimensionItems = collectDimensionItems(keysWithDimension);
            this.configWithoutDimension = buildConfigWithoutDimension(originalConfig, keysWithDimension);
        } else {
            definedDimensions = DefinedDimensions.NO_DIMENSIONS_DEFINED;
        }
    }

    private List<DimensionItem> collectDimensionItems(List<String> keysWithDimensionPrefix) {
        List<DimensionItem> items = keysWithDimensionPrefix.stream()
                .distinct()
                .map(this::parseDimension)
                .collect(Collectors.toList());

        Comparator<DimensionItem> rankItem = Comparator.comparingInt(DimensionItem::getRank);
        items.sort(rankItem);
        return items;
    }

    private Config buildConfigWithoutDimension(Config config, List<String> keysWithDimension) {
        Config newConfig = config.withoutPath(DIMENSIONS);

        for (String k : keysWithDimension) {
            int end = k.indexOf("\"", k.indexOf(DIMENSION_PREFIX)) + 1;
            newConfig = newConfig.withoutPath(k.substring(0, end));
        }

        return newConfig;
    }

    public static DimensionalConfig buildFrom(Config originalConfig) {
        return new DimensionalConfig(originalConfig);
    }

    public DimensionalConfigSelector selector() {
        return new DimensionalConfigSelector(this);
    }

    List<String> match(Map<String, String> map) {
        List<String> patterns = new ArrayList<>();
        int[] index = buildIndex(map);
        for (DimensionItem item : dimensionItems) {
            if (item.match(index)) {
                patterns.add(item.getPattern());
            }
        }

        return patterns;
    }

    private DimensionItem parseDimension(String pattern) {
        Map<String, String> dimensionMap = new HashMap<>(definedDimensions.getDimensionsCount());

        int start = pattern.indexOf(DIMENSION_PREFIX);
        int end = pattern.indexOf("\"", start);
        String patternPhase = pattern.substring(start + DIMENSION_PREFIX.length(), end);
        String[] pair = patternPhase.split(";");
        for (String p : pair) {
            String[] keyValue = p.split("=");
            dimensionMap.put(keyValue[0], keyValue[1]);
        }

        return new DimensionItem(patternPhase, buildIndex(dimensionMap));
    }

    private int[] buildIndex(Map<String, String> map) {
        int[] index = new int[definedDimensions.getDimensionKeys().size()];
        for (int i = 0; i < index.length; i++) {
            String key = definedDimensions.getDimensionKeys().get(i);
            if (map.containsKey(key)) {
                index[i] = definedDimensions.getAvailableDimensions().get(key).indexOf(map.get(key));
            } else {
                index[i] = -1;
            }
        }
        return index;
    }

    int getDimensionsCount() {
        return definedDimensions.getDimensionsCount();
    }

    boolean isDimensionAvailable() {
        return dimensionAvailable;
    }

    Config getOriginalConfig() {
        return originalConfig;
    }

    Config getConfigWithoutDimension() {
        return configWithoutDimension;
    }

    DefinedDimensions getDimensions() {
        return definedDimensions;
    }
}
