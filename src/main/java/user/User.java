package user;

import java.time.LocalDate;
import java.util.Objects;

public class User implements Comparable<User> {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return age == user.age && Objects.equals(userId, user.userId) && Objects.equals(name, user.name) && Objects.equals(dateOfBirth, user.dateOfBirth);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, name, age, dateOfBirth);
    }

    @Override
    public int compareTo(User o) {
        return this.userId.compareTo(o.userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
