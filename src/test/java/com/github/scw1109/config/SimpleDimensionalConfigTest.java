package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author scw1109
 */
public class SimpleDimensionalConfigTest {

    @Test
    public void testConfig_production_west_external() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "production")
                .set("cluster", "west")
                .set("network", "external")
                .getConfig();

        assertEquals(false, config.getBoolean("debug"));
        assertEquals(true, config.getBoolean("production"));
        assertEquals("production", config.getString("key-environment"));
        assertEquals("west", config.getString("key-cluster"));
        assertEquals("external", config.getString("key-network"));
        assertEquals("external-production", config.getString("key-k1"));
    }

    @Test
    public void testConfig_production_east_internal() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "production")
                .set("cluster", "east")
                .set("network", "internal")
                .getConfig();

        assertEquals(true, config.getBoolean("debug"));
        assertEquals(true, config.getBoolean("production"));
        assertEquals("production", config.getString("key-environment"));
        assertEquals("east", config.getString("key-cluster"));
        assertEquals("internal", config.getString("key-network"));
        assertEquals("internal-production", config.getString("key-k1"));
    }

    @Test
    public void testConfig_stage_east_internal() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "stage")
                .set("cluster", "east")
                .set("network", "internal")
                .getConfig();

        assertEquals(true, config.getBoolean("debug"));
        assertEquals(false, config.getBoolean("production"));
        assertEquals("stage", config.getString("key-environment"));
        assertEquals("east", config.getString("key-cluster"));
        assertEquals("internal", config.getString("key-network"));
        assertEquals("internal-stage", config.getString("key-k1"));

        assertEquals("1", config.getString("testing.a"));
        assertEquals("4", config.getString("testing.b"));
        assertEquals("3", config.getString("testing.c"));
    }

    @Test
    public void testConfig_production_west() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "production")
                .set("cluster", "west")
                .getConfig();

        assertEquals(true, config.getBoolean("debug"));
        assertEquals(true, config.getBoolean("production"));
        assertEquals("production", config.getString("key-environment"));
        assertEquals("west", config.getString("key-cluster"));
        assertEquals("production", config.getString("key-k1"));
    }

    @Test
    public void testConfig_stage_west() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "stage")
                .set("cluster", "west")
                .getConfig();

        assertEquals(true, config.getBoolean("debug"));
        assertEquals(false, config.getBoolean("production"));
        assertEquals("stage", config.getString("key-environment"));
        assertEquals("west", config.getString("key-cluster"));
        assertEquals("stage", config.getString("key-k1"));

        assertEquals("1", config.getString("testing.a"));
        assertEquals("2", config.getString("testing.b"));
        assertEquals(false, config.hasPath("testing.c"));
    }

    @Test
    public void testConfig_west_external() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("cluster", "west")
                .set("network", "external")
                .getConfig();

        assertEquals(false, config.getBoolean("debug"));
        assertEquals(false, config.getBoolean("production"));
        assertEquals("west", config.getString("key-cluster"));
        assertEquals("external", config.getString("key-network"));
        assertEquals("west", config.getString("key-k1"));
    }

    @Test
    public void testConfig_production() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "production")
                .getConfig();

        assertEquals(true, config.getBoolean("debug"));
        assertEquals(true, config.getBoolean("production"));
        assertEquals("production", config.getString("key-environment"));
        assertEquals("production", config.getString("key-k1"));
    }

    @Test
    public void testConfig_east() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("cluster", "east")
                .getConfig();

        assertEquals(true, config.getBoolean("debug"));
        assertEquals(false, config.getBoolean("production"));
        assertEquals("east", config.getString("key-cluster"));
        assertEquals("east", config.getString("key-k1"));
    }

    @Test
    public void testConfig_external() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("network", "external")
                .getConfig();

        assertEquals(false, config.getBoolean("debug"));
        assertEquals(false, config.getBoolean("production"));
        assertEquals("external", config.getString("key-network"));
        assertEquals("external", config.getString("key-k1"));
    }

    @Test
    public void testConfig_default() {
        Config originalConfig = ConfigFactory.load("simple-dimensional-config");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .getConfig();

        assertEquals(true, config.getBoolean("debug"));
        assertEquals(false, config.getBoolean("production"));
        assertEquals("default", config.getString("key-k1"));
    }
}
