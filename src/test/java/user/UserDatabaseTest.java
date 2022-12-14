package user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserDatabaseTest {
    private final UserDatabase userDatabase = new UserDatabase();

    @Test
    void write_and_read_a_user() {
        User user = new User(new UserId(1), "Pippo", 43, LocalDate.of(1975, 9, 21));
        userDatabase.write(user);

        Optional<User> retrievedUser = userDatabase.read(new UserId(1));

        assertTrue(retrievedUser.isPresent());
        assertEquals(retrievedUser.get(), user);
    }

    @Test
    void write_and_read_user_in_batch() {
        User user1 = new User(new UserId(1), "Pippo", 43, LocalDate.of(1975, 9, 21));
        User user2 = new User(new UserId(2), "Pluto", 50, LocalDate.of(1975, 10, 22));
        User user3 = new User(new UserId(3), "Minni", 53, LocalDate.of(1980, 11, 12));
        userDatabase.writeAll(user1, user2, user3);

        List<User> users = userDatabase.readAll(new UserId(3), new UserId(1), new UserId(2));

        assertEquals(users.get(0), user3);
        assertEquals(users.get(1), user1);
        assertEquals(users.get(2), user2);
    }

    @Test
    void return_optional_user_from_read() {
        Optional<User> retrievedUser = userDatabase.read(new UserId(1));

        assertTrue(retrievedUser.isEmpty());
    }

    @Test
    void return_only_present_users_from_read_all() {
        User user1 = new User(new UserId(1), "Pippo", 43, LocalDate.of(1975, 9, 21));
        userDatabase.writeAll(user1);

        List<User> users = userDatabase.readAll(new UserId(1), new UserId(2), new UserId(3));

        assertEquals(1, users.size());
        assertEquals(user1, users.get(0));
    }
}
