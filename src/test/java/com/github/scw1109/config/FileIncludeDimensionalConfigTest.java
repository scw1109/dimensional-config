package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author scw1109
 */
public class FileIncludeDimensionalConfigTest {

    @Test
    public void testConfig_production_internal() {
        Config originalConfig = ConfigFactory.load("project1/project1");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "production")
                .set("network", "internal")
                .getConfig();

        assertEquals(true, config.getBoolean("app1.enable"));
        assertEquals(true, config.getBoolean("app1.debug"));
        assertEquals(true, config.getBoolean("app2.enable"));

        assertEquals(true, config.hasPath("debug-settings"));
        assertEquals(1, config.getInt("debug-settings.a"));
        assertEquals(2, config.getInt("debug-settings.b"));
    }

    @Test
    public void testConfig_production_external() {
        Config originalConfig = ConfigFactory.load("project1/project1");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "production")
                .set("network", "external")
                .getConfig();

        assertEquals(true, config.getBoolean("app1.enable"));
        assertEquals(false, config.hasPath("app1.debug"));
        assertEquals(true, config.getBoolean("app2.enable"));

        assertEquals(false, config.hasPath("debug-settings"));
    }

    @Test
    public void testConfig_default() {
        Config originalConfig = ConfigFactory.load("project1/project1");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .getConfig();

        assertEquals(false, config.getBoolean("app1.enable"));
        assertEquals(false, config.hasPath("app1.debug"));
        assertEquals(false, config.getBoolean("app2.enable"));

        assertEquals(false, config.hasPath("debug-settings"));
    }
}
