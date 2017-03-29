package com.github.scw1109.config;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author scw1109
 */
public class RealCaseDimensionalConfigTest {

    @Test
    public void testConfig_production() {
        Config originalConfig = ConfigFactory.load("servant/application");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "production")
                .getConfig();

        assertEquals(6666, config.getInt("servant.server.port"));
        List<? extends Config> configList = config.getConfigList("servant.connectors");
        assertEquals(4, configList.size());
        assertEquals("servant-line", configList.get(1).getString("id"));
    }

    @Test
    public void testConfig_development() {
        Config originalConfig = ConfigFactory.load("servant/application");
        DimensionalConfig dimensionalConfig = DimensionalConfig.buildFrom(originalConfig);
        Config config = dimensionalConfig.selector()
                .set("environment", "development")
                .getConfig();

        assertEquals(7777, config.getInt("servant.server.port"));
        List<? extends Config> configList = config.getConfigList("servant.connectors");
        assertEquals(2, configList.size());
        assertEquals("servant-slack-rtm", configList.get(1).getString("id"));
    }
}
