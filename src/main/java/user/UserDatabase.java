package user;


import redis.RedisClientFactory;
import redis.clients.jedis.Jedis;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class UserDatabase {
    private final Jedis redis = new RedisClientFactory().createClient();

    public void write(User user) {
        Map<String, String> serializedUser = Map.of(
                "name", user.name(),
                "age", String.valueOf(user.age()),
                "date-of-birth", user.dateOfBirth().format(DateTimeFormatter.ISO_LOCAL_DATE));

        redis.hset(userKey(user.userId()), serializedUser);
    }

    public User read(UserId userId) {
        Map<String, String> serializedUser = redis.hgetAll(userKey(userId));

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
