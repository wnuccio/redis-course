package search;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import redis.RedisClientFactory;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.IndexDefinition;
import redis.clients.jedis.search.IndexOptions;
import redis.clients.jedis.search.Schema;
import redis.clients.jedis.search.SearchResult;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


public class RedisSearchTest {
    private JedisPooled client;

    @BeforeEach
    void setup() {
        client = new RedisClientFactory().loginToRedisWithModulesAndFlushDb();
    }

    @Test
    void name() {
        client.hset("user#1", Map.of("name", "Walter", "age", "47"));
        client.hset("user#2", Map.of("name", "Andrea", "age", "35"));
        client.hset("user#3", Map.of("name", "Carmine", "age", "38"));

        Schema schema = new Schema()
                .addTextField("name", 5.0)
                .addNumericField("age");

        IndexDefinition indexDefinition = new IndexDefinition()
                .setPrefixes("user#")
                .setFilter("@age>40");

        client.ftCreate("idx:users", IndexOptions.defaultOptions().setDefinition(indexDefinition), schema);

        SearchResult result = client.ftSearch("idx:users");

        assertThat(result.getTotalResults()).isEqualTo(1);
        assertThat(result.getDocuments().get(0).get("name")).isEqualTo("Walter");
        assertThat(result.getDocuments().get(0).get("age")).isEqualTo("47");
    }
}
