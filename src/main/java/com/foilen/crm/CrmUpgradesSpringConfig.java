package com.foilen.crm;

import com.foilen.smalltools.upgrader.UpgraderTools;
import com.foilen.smalltools.upgrader.tasks.UpgradeTask;
import com.foilen.smalltools.upgrader.trackers.MongoDbUpgraderTracker;
import com.foilen.smalltools.upgrader.trackers.UpgraderTracker;
import com.mongodb.client.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@ComponentScan({"com.foilen.crm.upgrades"})
@EnableAutoConfiguration
@PropertySource({"classpath:/com/foilen/crm/application-common.properties", "classpath:/com/foilen/crm/application-${MODE}.properties"})
public class CrmUpgradesSpringConfig {

    @Autowired
    private MongoClient mongoClient;

    @Value("${spring.mongodb.database}")
    private String databaseName;

    @Bean
    public UpgraderTracker mongodbUpgraderTracker() {
        return new MongoDbUpgraderTracker(mongoClient, databaseName);
    }

    @Bean
    public UpgraderTools upgraderTools(List<UpgradeTask> tasks) {
        UpgraderTools upgraderTools = new UpgraderTools(tasks);
        upgraderTools.setDefaultUpgraderTracker(mongodbUpgraderTracker());
        upgraderTools.getUpgraderTrackerByName().put("mongodb", mongodbUpgraderTracker());
        return upgraderTools;
    }

}
