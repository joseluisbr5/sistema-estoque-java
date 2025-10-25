package com.sistema.estoque;

import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Updates.combine;
import static com.mongodb.client.model.Updates.set;

public class ProdutoDAO {
    private final MongoCollection<Document> collection;
    private final Jedis redis;
    private static final String CACHE_KEY_ALL = "produtos:all";
    private static final int CACHE_TTL_SECONDS = 60;

    public ProdutoDAO() {
        MongoDatabase db = MongoConnection.getDatabase();
        this.collection = db.getCollection("produtos");
        this.redis = RedisConnection.getJedis();
    }

    public String criar(Produto p) {
        Document doc = new Document("nome", p.getNome())
                .append("preco", p.getPreco())
                .append("quantidade", p.getQuantidade())
                .append("categoria", p.getCategoria());
        collection.insertOne(doc);
        String id = doc.getObjectId("_id").toHexString();
        p.setId(id);
        invalidateCache();
        return id;
    }

    public List<Produto> listarTodos() {
        String cached = redis.get(CACHE_KEY_ALL);
        if (cached != null) {
            java.lang.reflect.Type listType = new TypeToken<List<Produto>>(){}.getType();
            return JsonUtil.get().fromJson(cached, listType);
        }

        List<Produto> result = new ArrayList<>();
        for (Document d : collection.find()) {
            Produto p = new Produto(
                    d.getString("nome"),
                    d.getDouble("preco"),
                    d.getInteger("quantidade", 0),
                    d.getString("categoria")
            );
            ObjectId oid = d.getObjectId("_id");
            if (oid != null) p.setId(oid.toHexString());
            result.add(p);
        }

        redis.setex(CACHE_KEY_ALL, CACHE_TTL_SECONDS, JsonUtil.get().toJson(result));
        // setex(seconds, key, value)
        return result;
    }

    public Produto buscarPorId(String id) {
        Document d = collection.find(eq("_id", new ObjectId(id))).first();
        if (d == null) return null;
        Produto p = new Produto(
                d.getString("nome"),
                d.getDouble("preco"),
                d.getInteger("quantidade", 0),
                d.getString("categoria")
        );
        ObjectId oid = d.getObjectId("_id");
        if (oid != null) p.setId(oid.toHexString());
        return p;
    }

    public boolean atualizar(String id, Produto novo) {
        var result = collection.updateOne(eq("_id", new ObjectId(id)),
                combine(
                        set("nome", novo.getNome()),
                        set("preco", novo.getPreco()),
                        set("quantidade", novo.getQuantidade()),
                        set("categoria", novo.getCategoria())
                ));
        boolean ok = result.getModifiedCount() > 0;
        if (ok) invalidateCache();
        return ok;
    }

    public boolean excluir(String id) {
        var result = collection.deleteOne(eq("_id", new ObjectId(id)));
        boolean ok = result.getDeletedCount() > 0;
        if (ok) invalidateCache();
        return ok;
    }

    private void invalidateCache() {
        redis.del(CACHE_KEY_ALL);
    }
}
