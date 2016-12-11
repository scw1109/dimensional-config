package com.github.scw1109.config;

/**
 * @author scw1109
 */
public class DimensionItem {

    private final String pattern;
    private final int[] dimensionIndex;
    private int rank;

    public DimensionItem(String pattern, int[] dimensionIndex) {
        this.pattern = pattern;
        this.dimensionIndex = dimensionIndex;

        for (int i = 0; i < dimensionIndex.length; i++) {
            if (dimensionIndex[i] != -1) {
                rank += (dimensionIndex.length - i) * dimensionIndex.length + (i + 1);
            }
        }
    }

    boolean match(int[] index) {
        for (int i = 0; i < dimensionIndex.length; i++) {
            if (dimensionIndex[i] != -1 &&
                    dimensionIndex[i] != index[i]) {
                return false;
            }
        }
        return true;
    }

    public String getPattern() {
        return pattern;
    }

    public int getRank() {
        return rank;
    }
}
