package com.sistema.estoque;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoConnection {
    private static MongoDatabase database;

    public static MongoDatabase getDatabase() {
        if (database == null) {
            MongoClient client = MongoClients.create(Config.MONGO_URI);
            database = client.getDatabase(Config.DB_NAME);
            System.out.println("✅ Conectado ao MongoDB Atlas — DB: " + Config.DB_NAME);
        }
        return database;
    }
}
