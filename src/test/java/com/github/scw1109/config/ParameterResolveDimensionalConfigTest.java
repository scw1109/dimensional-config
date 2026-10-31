package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParameterResolveDimensionalConfigTest {

    @Test
    public void testBaseSubstitution() {
        Config originalConfig = ConfigFactory.parseResources("parameter-resolve.conf");
        assertFalse(originalConfig.isResolved());
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);

        Config config = dimensionalConfig.selector().getConfig();

        assertEquals("base-value", config.getString("resolved-value"));
    }

    @Test
    public void testSubstitutionUsesBaseForNonMatchingDimension() {
        Config originalConfig = ConfigFactory.parseResources("parameter-resolve.conf");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);

        Config config = dimensionalConfig.selector()
                .set("environment", "stage")
                .getConfig();

        assertEquals("base-value", config.getString("source-value"));
        assertEquals("base-value", config.getString("resolved-value"));
    }

    @Test
    public void testMissingReferenceFailsAtGetConfig() {
        Config originalConfig = ConfigFactory.parseResources("parameter-resolve-missing.conf");
        assertFalse(originalConfig.isResolved());
        DimensionalConfigSelector selector = DimensionalConfig.buildFrom(originalConfig).selector();

        assertThrows(ConfigException.UnresolvedSubstitution.class, selector::getConfig);
    }
}
