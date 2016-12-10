package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            Map<String, Object> map = configRoot.unwrapped();
            List<String> keysWithDimensionPrefix = map.keySet().stream()
                    .filter(k -> k.contains(DIMENSION_PREFIX))
                    .collect(Collectors.toList());

            this.dimensionItems = collectDimensionItems(keysWithDimensionPrefix);
            this.configWithoutDimension = buildConfigWithoutDimension(configRoot, keysWithDimensionPrefix);
        } else {
            definedDimensions = DefinedDimensions.NO_DIMENSIONS_DEFINED;
        }
    }

    private List<DimensionItem> collectDimensionItems(List<String> keysWithDimensionPrefix) {
        List<DimensionItem> dimensionItems = keysWithDimensionPrefix.stream()
                .distinct()
                .map(this::parseDimension)
                .collect(Collectors.toList());

        Comparator<DimensionItem> rankItem = Comparator.comparingInt(DimensionItem::getRank);
        dimensionItems.sort(rankItem.reversed());
        return dimensionItems;
    }

    private Config buildConfigWithoutDimension(ConfigObject configRoot, List<String> keysWithDimensionPrefix) {
        ConfigObject configWithoutDimension = configRoot.withoutKey(DIMENSIONS);

        for (String k : keysWithDimensionPrefix) {
            configWithoutDimension = configWithoutDimension.withoutKey(k);
        }

        return configWithoutDimension.toConfig();
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

        pattern = pattern.substring(DIMENSION_PREFIX.length());
        String[] pair = pattern.split(";");
        for (String p : pair) {
            String[] keyValue = p.split("=");
            dimensionMap.put(keyValue[0], keyValue[1]);
        }

        return new DimensionItem(pattern, buildIndex(dimensionMap));
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
