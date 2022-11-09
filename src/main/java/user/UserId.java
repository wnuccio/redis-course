package user;

import java.util.Objects;

public class UserId {
    private final int id;

    public UserId(int id) {
        this.id = id;
    }

    public int value() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserId userId = (UserId) o;
        return id == userId.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "UserId{" +
                "id=" + id +
                '}';
    }
}
