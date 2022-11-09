package user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDatabaseTest {
    private final UserDatabase userDatabase = new UserDatabase();

    @Test
    void write_and_read_a_user() {
        User user = new User(new UserId(1), "Pippo", 43, LocalDate.of(1975, 9, 21));
        userDatabase.write(user);

        User retrievedUser = userDatabase.read(new UserId(1));

        assertEquals(retrievedUser.name(), "Pippo");
        assertEquals(retrievedUser.age(), 43);
        assertEquals(retrievedUser.dateOfBirth(), LocalDate.of(1975, 9, 21));
    }

    @Test
    void write_and_read_user_in_batch() {
        User user1 = new User(new UserId(1), "Pippo", 43, LocalDate.of(1975, 9, 21));
        User user2 = new User(new UserId(2), "Pluto", 50, LocalDate.of(1975, 10, 22));
        userDatabase.writeAll(user1, user2);

        List<User> users = userDatabase.readAll(new UserId(1), new UserId(2));

        assertEquals(users.get(0), user1);
        assertEquals(users.get(1), user2);
    }
}
