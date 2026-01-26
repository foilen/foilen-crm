package com.foilen.crm;

import com.foilen.smalltools.upgrader.UpgraderTools;
import com.foilen.smalltools.upgrader.tasks.UpgradeTask;
import com.foilen.smalltools.upgrader.trackers.DatabaseUpgraderTracker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
@ComponentScan({"com.foilen.crm.upgrades"})
@EnableAutoConfiguration
@PropertySource({"classpath:/com/foilen/crm/application-common.properties", "classpath:/com/foilen/crm/application-${MODE}.properties"})
public class CrmUpgradesSpringConfig {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Bean
    public UpgraderTools upgraderTools(List<UpgradeTask> tasks) {
        UpgraderTools upgraderTools = new UpgraderTools(tasks);
        upgraderTools.addUpgraderTracker("db", new DatabaseUpgraderTracker(jdbcTemplate));
        return upgraderTools;
    }

}
