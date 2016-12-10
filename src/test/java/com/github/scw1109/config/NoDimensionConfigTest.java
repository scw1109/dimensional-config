package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author scw1109
 */
public class NoDimensionConfigTest {

    @Test
    public void testConfig() {
        Config originalConfig = ConfigFactory.load("no-dimension-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector().getConfig();

        assertEquals("1", config.getString("key1"));
        assertEquals("1", config.getString("app.key1"));
        assertEquals("2", config.getString("app.key2"));
    }
}
