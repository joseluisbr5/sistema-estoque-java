# Sistema de Estoque — Java (sem Maven) • MongoDB + Redis + Swing

Projeto **simples** (sem Maven) para a disciplina **Banco de Dados Avançados**:
- CRUD de **Produtos** no **MongoDB Atlas**
- **Cache** de listagem no **Redis (Jedis)**
- **Interface Gráfica (Swing)** para bônus de +0,5

## Estrutura
```
sistema-estoque-java/
├── lib/                             # coloque aqui os .jar (Mongo, Jedis, Gson)
└── src/com/sistema/estoque/
    ├── AppConsole.java              # (opcional) main de console
    ├── EstoqueGUI.java              # main da interface Swing
    ├── Produto.java                 # modelo
    ├── ProdutoDAO.java              # CRUD + cache
    ├── MongoConnection.java         # conexão com MongoDB
    ├── RedisConnection.java         # conexão com Redis
    ├── JsonUtil.java                # Gson helper
    └── Config.java                  # configurações (env vars)
```

## Dependências (baixe os .JAR e coloque em `lib/`)
- MongoDB Java Driver (Sync) — ex.: `mongodb-driver-sync-5.1.0.jar`
- Jedis (Redis) — ex.: `jedis-5.1.0.jar`
- Gson — ex.: `gson-2.11.0.jar`

Baixe em: https://mvnrepository.com (busque pelos nomes acima).
> Dica: se preferir, use Docker pro Redis: `docker run -p 6379:6379 redis:latest`

## Configurações (variáveis de ambiente)
Defina no **Run/Debug Configuration** do IntelliJ (ou no terminal):
```
MONGO_URI=mongodb+srv://usuario:senha@cluster.mongodb.net/
DB_NAME=estoque
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASS=        (opcional; deixe vazio se não houver senha)
```

## Como compilar/executar no IntelliJ (sem Maven)
1. **File > Open...** e selecione a pasta `sistema-estoque-java`.
2. **Project Structure (Ctrl+Alt+Shift+S)** → `Modules` → aba `Dependencies` → clique `+` → **JARs or Directories** → selecione todos os `.jar` em `lib/`.
3. Crie uma **Run Configuration** do tipo **Application**:
   - Main class **(GUI)**: `com.sistema.estoque.EstoqueGUI`
   - (opcional) Main class **(console)**: `com.sistema.estoque.AppConsole`
   - Sete as **Environment variables** acima.
4. **Run ▶️**

## O que demonstrar no vídeo
- Abrir o programa (GUI), cadastrar 1–2 produtos, listar, atualizar e excluir.
- Mostrar o **cache**: após a primeira listagem, a próxima vem do Redis por ~60s.
- Mostrar a conexão no console (mensagens ✅).

> Observação: nunca exponha usuário/senha do Atlas no GitHub. Use `MONGO_URI` via ambiente.
Boa prova! 🎓
