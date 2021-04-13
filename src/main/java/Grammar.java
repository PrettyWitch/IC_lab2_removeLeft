import java.util.ArrayList;
import java.util.List;

/**
 * @author yuhan
 * @date 03.04.2021 - 11:19
 * @purpose 文法
 */
public class Grammar {
    //文法的名称
    private String name;
    private List<term> terminalSymbols = new ArrayList<>();
    private List<nonterm> nonterminalSymbols= new ArrayList<>();
    private List<production> productions = new ArrayList<>();
    private String startSymbol;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<term> getTerminalSymbols() {
        return terminalSymbols;
    }

    public void setTerminalSymbols(List<term> terminalSymbols) {
        this.terminalSymbols = terminalSymbols;
    }

    public List<nonterm> getNonterminalSymbols() {
        return nonterminalSymbols;
    }

    public void setNonterminalSymbols(List<nonterm> nonterminalSymbols) {
        this.nonterminalSymbols = nonterminalSymbols;
    }

    public List<production> getProductions() {
        return productions;
    }

    public void setProductions(List<production> productions) {
        this.productions = productions;
    }

    public String getStartSymbol() {
        return startSymbol;
    }

    public void setStartSymbol(String startSymbol) {
        this.startSymbol = startSymbol;
    }

    @Override
    public String toString() {
        return "Grammar{" +
                "name='" + name + '\'' +
                ", terminalSymbols=" + terminalSymbols +
                ", nonterminalSymbols=" + nonterminalSymbols +
                ", productions=" + productions +
                ", startSymbol='" + startSymbol + '\'' +
                '}';
    }
}
