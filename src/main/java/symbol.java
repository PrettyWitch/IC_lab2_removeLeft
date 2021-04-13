import java.util.Objects;

/**
 * @author yuhan
 * @date 03.04.2021 - 11:28
 * @purpose
 */
public class symbol {
    private String name;
    private String type;

    public symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "symbol{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        symbol symbol = (symbol) o;
        return Objects.equals(name, symbol.name) && Objects.equals(type, symbol.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }
}
