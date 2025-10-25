package com.sistema.estoque;

import redis.clients.jedis.Jedis;

public class RedisConnection {
    private static Jedis jedis;

    public static Jedis getJedis() {
        if (jedis == null) {
            jedis = new Jedis(Config.REDIS_HOST, Config.REDIS_PORT);
            if (Config.REDIS_PASS != null && !Config.REDIS_PASS.isBlank()) {
                jedis.auth(Config.REDIS_PASS);
            }
            System.out.println("✅ Conectado ao Redis em " + Config.REDIS_HOST + ":" + Config.REDIS_PORT);
        }
        return jedis;
    }
}
