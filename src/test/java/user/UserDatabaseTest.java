package user;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserDatabaseTest {

    @Test
    void write_and_read_a_user() {
        User user = new User(new UserId(1), "Pippo", 43, LocalDate.of(1975, 9, 21));

        UserDatabase userDatabase = new UserDatabase();

        userDatabase.write(user);

        User retrievedUser = userDatabase.read(new UserId(1));

        assertEquals(retrievedUser.name(), "Pippo");
        assertEquals(retrievedUser.age(), 43);
        assertEquals(retrievedUser.dateOfBirth(), LocalDate.of(1975, 9, 21));
    }
}
