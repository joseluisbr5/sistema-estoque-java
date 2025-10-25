package com.sistema.estoque;

import redis.clients.jedis.Jedis;

import java.util.UUID;

public class AuthService {

    private static final int SESSION_TTL_SECONDS = 300; // sessão dura 5 minutos

    public String login(String usuario) {
        try (Jedis jedis = RedisConnection.getJedis()) {
            String token = UUID.randomUUID().toString(); // gera um token único
            jedis.setex("sessao:" + usuario, SESSION_TTL_SECONDS, token);
            return token;
        }
    }

    public boolean verificarSessao(String usuario, String token) {
        try (Jedis jedis = RedisConnection.getJedis()) {
            String tokenSalvo = jedis.get("sessao:" + usuario);
            return tokenSalvo != null && tokenSalvo.equals(token);
        }
    }

    public void logout(String usuario) {
        try (Jedis jedis = RedisConnection.getJedis()) {
            jedis.del("sessao:" + usuario);
        }
    }
}
