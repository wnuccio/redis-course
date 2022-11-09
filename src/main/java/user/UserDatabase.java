package user;


import redis.RedisClientFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserDatabase {
    private final Jedis redis = new RedisClientFactory().createClient();

    public void write(User user) {
        Map<String, String> serializedUser = serialize(user);

        redis.hset(userKey(user.userId()), serializedUser);
    }

    public User read(UserId userId) {
        Map<String, String> serializedUser = redis.hgetAll(userKey(userId));

        return unserialize(userId, serializedUser);
    }

    public void writeAll(User... users) {
        Pipeline pipeline = redis.pipelined();

        for (User user : users) {
            Map<String, String> serialized = serialize(user);
            pipeline.hset(userKey(user.userId()), serialized);
        }

        pipeline.sync();
    }

    public List<User> readAll(UserId... userIds) {
        Pipeline pipeline = redis.pipelined();

        Map<UserId, Response<Map<String, String>>> userMap = new HashMap<>();
        for (UserId userId : userIds) {
            Response<Map<String, String>> response = pipeline.hgetAll(userKey(userId));
            userMap.put(userId, response);
        }
        pipeline.sync();


        return userMap.entrySet().stream()
                .map(entry -> {
                    UserId userId = entry.getKey();
                    Response<Map<String, String>> serializedUser = entry.getValue();
                    return unserialize(userId, serializedUser.get());
                })
                .sorted()
                .collect(Collectors.toList());
    }

    private Map<String, String> serialize(User user) {
        return Map.of(
                "name", user.name(),
                "age", String.valueOf(user.age()),
                "date-of-birth", user.dateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE));
    }

    private User unserialize(UserId userId, Map<String, String> serializedUser) {
        return new User(
                userId,
                serializedUser.get("name"),
                Integer.parseInt(serializedUser.get("age")),
                LocalDate.parse(serializedUser.get("date-of-birth"), DateTimeFormatter.ISO_LOCAL_DATE)
        );
    }

    private String userKey(UserId userId) {
        return "users" + "#" + userId.value();
    }
}
