package user;

import java.time.LocalDate;

public class User {

    private final UserId userId;
    private final String name;
    private final int age;
    private final LocalDate dateOfBirth;

    public User(UserId userId, String name, int age, LocalDate dateOfBirth) {
        this.userId = userId;
        this.name = name;
        this.age = age;
        this.dateOfBirth = dateOfBirth;
    }

    public UserId userId() {
        return userId;
    }

    public String name() {
        return name;
    }

    public int age() {
        return age;
    }

    public LocalDate dateOfBirth() {
        return dateOfBirth;
    }
}
