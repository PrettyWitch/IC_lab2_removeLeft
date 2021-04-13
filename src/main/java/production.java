import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author yuhan
 * @date 03.04.2021 - 11:27
 * @purpose
 */
public class production {
    private String lhs;
    private List<symbol> rhs = new ArrayList<>();

    public String getLhs() {
        return lhs;
    }

    public void setLhs(String lhs) {
        this.lhs = lhs;
    }

    public List<symbol> getRhs() {
        return rhs;
    }

    public void setRhs(List<symbol> rhs) {
        this.rhs = rhs;
    }

    @Override
    public String toString() {
        return "production{" +
                "lhs='" + lhs + '\'' +
                ", rhs=" + rhs +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        production that = (production) o;
        return Objects.equals(lhs, that.lhs) && Objects.equals(rhs, that.rhs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(lhs, rhs);
    }
}
