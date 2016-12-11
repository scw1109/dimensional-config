package com.github.scw1109.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author scw1109
 */
public class DefinedDimensions {

    public static final DefinedDimensions NO_DIMENSIONS_DEFINED = new NoDefinedDimensions();

    private List<String> dimensionKeys = Collections.emptyList();
    private Map<String, List<String>> availableDimensions = Collections.emptyMap();

    @SuppressWarnings("WeakerAccess")
    public List<String> getDimensionKeys() {
        return dimensionKeys;
    }

    @SuppressWarnings("unused")
    public void setDimensionKeys(List<String> dimensionKeys) {
        this.dimensionKeys = dimensionKeys;
    }

    @SuppressWarnings("WeakerAccess")
    public Map<String, List<String>> getAvailableDimensions() {
        return availableDimensions;
    }

    @SuppressWarnings("unused")
    public void setAvailableDimensions(Map<String, Object> availableDimensions) {
        this.availableDimensions = new HashMap<>(availableDimensions.size());
        for (Map.Entry<String, Object> entry : availableDimensions.entrySet()) {
            if (entry.getValue() instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) entry.getValue();
                this.availableDimensions.put(entry.getKey(), list);
            }
        }
    }

    int getDimensionsCount() {
        return dimensionKeys.size();
    }

    static class NoDefinedDimensions extends DefinedDimensions {

        @Override
        public void setDimensionKeys(List<String> dimensionKeys) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setAvailableDimensions(Map<String, Object> availableDimensions) {
            throw new UnsupportedOperationException();
        }
    }
}
