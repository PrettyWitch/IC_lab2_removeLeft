/**
 * @author yuhan
 * @date 03.04.2021 - 11:20
 * @purpose 终止符
 */
public class term {
    private String name;
    private String spell;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpell() {
        return spell;
    }

    public void setSpell(String spell) {
        this.spell = spell;
    }

    @Override
    public String toString() {
        return "term{" +
                "name='" + name + '\'' +
                ", spell='" + spell + '\'' +
                '}';
    }
}

