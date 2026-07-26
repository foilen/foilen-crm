package com.foilen.crm.upgrades;

import com.foilen.smalltools.upgrader.trackers.AbstractMongoUpgradeTask;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The "email" field is now the "user" collection's _id (replacing the previous auto-generated one).
 * Mongo cannot change a document's _id in place, so each user is recreated under its email and the
 * old document is deleted.
 */
@Component
public class V_20260727_02_User_EmailAsId extends AbstractMongoUpgradeTask {

    @Override
    public void execute() {

        MongoDatabase mongoDatabase = mongoClient.getDatabase(databaseName);
        MongoCollection<Document> collection = mongoDatabase.getCollection("user");

        List<Document> existingUsers = new ArrayList<>();
        collection.find().into(existingUsers);

        Set<String> usedEmail = new HashSet<>();
        for (Document existingUser : existingUsers) {
            Object oldId = existingUser.get("_id");
            String email = existingUser.getString("email");

            if (email == null) {
                logger.warn("Skipping user {} without an email", oldId);
                continue;
            }
            if (email.equals(oldId)) {
                // Already using the email as _id
                usedEmail.add(email);
                continue;
            }
            if (!usedEmail.add(email)) {
                // Another user already has this email as _id: keep only one
                logger.warn("Skipping user {} since another user already uses email {} as _id", oldId, email);
                collection.deleteOne(new Document("_id", oldId));
                continue;
            }

            logger.info("Migrating user {} to _id {}", oldId, email);
            Document newUser = new Document(existingUser);
            newUser.remove("email");
            newUser.put("_id", email);

            collection.insertOne(newUser);
            collection.deleteOne(new Document("_id", oldId));
        }

    }

}
