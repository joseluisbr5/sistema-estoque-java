package com.sistema.estoque;

public class Config {
    // Variáveis de ambiente com defaults
    public static final String MONGO_URI = getenvOr("MONGO_URI", "mongodb+srv://joseluis123br_db_user:joseluis2025@cluster0.dzipgxi.mongodb.net/");
    public static final String DB_NAME   = getenvOr("DB_NAME", "estoque");
    public static final String REDIS_HOST = getenvOr("REDIS_HOST", "localhost");
    public static final int    REDIS_PORT = Integer.parseInt(getenvOr("REDIS_PORT", "6379"));
    public static final String REDIS_PASS = getenvOr("REDIS_PASS", "");

    private static String getenvOr(String k, String def) {
        String v = System.getenv(k);
        return (v == null || v.isEmpty()) ? def : v;
    }
}
