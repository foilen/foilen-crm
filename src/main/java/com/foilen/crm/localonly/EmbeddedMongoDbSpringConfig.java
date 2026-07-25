package com.foilen.crm.localonly;

import com.foilen.smalltools.tools.AbstractBasics;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.commands.MongodArguments;
import de.flapdoodle.embed.mongo.config.Storage;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.mongo.transitions.Mongod;
import de.flapdoodle.embed.mongo.transitions.RunningMongodProcess;
import de.flapdoodle.reverse.StateID;
import de.flapdoodle.reverse.Transition;
import de.flapdoodle.reverse.transitions.Start;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

/**
 * Boots an embedded, ephemeral MongoDB (single-node replica set, needed for transactions) for local development and JUnit tests.
 * No external MongoDB container needed for these profiles.
 */
@Configuration
@Profile({"JUNIT", "LOCAL"})
public class EmbeddedMongoDbSpringConfig extends AbstractBasics {

    @Value("${spring.mongodb.database}")
    private String databaseName;

    @Bean
    public MongoClient mongoClient() {
        var running = new Mongod() {
            @Override
            public Transition<MongodArguments> mongodArguments() {
                return Start.to(MongodArguments.class)
                        .initializedWith(MongodArguments.defaults()
                                .withReplication(Storage.of("rs", 5000))
                        );
            }
        }.transitions(Version.Main.V8_2)
                .walker()
                .initState(StateID.of(RunningMongodProcess.class));
        Runtime.getRuntime().addShutdownHook(new Thread(running::close));

        String uriForAdmin = "mongodb://" + running.current().getServerAddress().toString();
        MongoClient adminMongoClient = MongoClients.create(uriForAdmin);
        adminMongoClient.getDatabase("admin").runCommand(new Document("replSetInitiate", new Document()));

        String uriWithRs = "mongodb://" + running.current().getServerAddress().toString() + "/?replicaSet=rs";
        return MongoClients.create(uriWithRs);
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        return new SimpleMongoClientDatabaseFactory(mongoClient(), databaseName);
    }

    @Bean
    public MongoTemplate mongoTemplate(MappingMongoConverter mongoConverter) {
        return new MongoTemplate(mongoDatabaseFactory(), mongoConverter);
    }

}
