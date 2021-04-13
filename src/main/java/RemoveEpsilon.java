import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuhan
 * @date 09.04.2021 - 13:57
 * @purpose
 */
public class RemoveEpsilon {
    //读取json文件，并进行解析
    public Grammar readJson(String path) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(path);
        String text = IOUtils.toString(inputStream, "UTF-8");
        text = JSONObject.parseObject(text).get("grammar").toString().replace("-", "");
        JSONObject g = JSONObject.parseObject(text);
        //注入实体类
        Grammar grammar = new Grammar();
        grammar.setName(g.getString("name"));
        grammar.setTerminalSymbols(g.getJSONObject("terminalsymbols").getJSONArray("term").toJavaList(term.class));
        grammar.setNonterminalSymbols(g.getJSONObject("nonterminalsymbols").getJSONArray("nonterm").toJavaList(nonterm.class));
        List<production> productions = grammar.getProductions();
        JSONArray jsonArray = g.getJSONObject("productions").getJSONArray("production");
        for (int i = 0; i < jsonArray.size(); i++) {
            productions.add(new production());
            productions.get(i).setLhs(jsonArray.getJSONObject(i).getJSONObject("lhs").getString("name"));
            String symbols = jsonArray.getJSONObject(i).getJSONObject("rhs").getString("symbol");
            if (symbols.contains("[")) {
                productions.get(i).setRhs(JSON.parseArray(symbols, symbol.class));
            } else
                productions.get(i).getRhs().add(JSONObject.parseObject(symbols, symbol.class));
        }
        grammar.setStartSymbol(g.getJSONObject("startsymbol").getString("name"));
        return grammar;
    }

    public void showGrammar(Grammar g) {
        System.out.println("NonterminalSymbols: " + g.getNonterminalSymbols().size());
        for (nonterm nonterm : g.getNonterminalSymbols()) {
            System.out.print(nonterm.getName() + " ");
        }
        System.out.println("\nterminalSymbols: " + g.getTerminalSymbols().size());
        for (term term : g.getTerminalSymbols()) {
            System.out.print(term.getSpell() + " ");
        }
        System.out.println("\nproductions: " + g.getProductions().size());
        for (production production : g.getProductions()) {
            System.out.print(production.getLhs() + "-->");
            for (symbol symbol : production.getRhs()) {
                switch (symbol.getName()) {
                    case "ADD":
                        System.out.print("+ ");
                        break;
                    case "MUL":
                        System.out.print("* ");
                        break;
                    case "LPAREN":
                        System.out.print("( ");
                        break;
                    case "RPAREN":
                        System.out.print(") ");
                        break;
                    default:
                        System.out.print(symbol.getName() + " ");
                }
            }
            System.out.println();
        }
        System.out.println("StartSymbol: " + g.getStartSymbol());
    }

    public void eliminateEpsilon2(Grammar grammar) {
        List<production> productions = grammar.getProductions();
        int size = grammar.getNonterminalSymbols().size();
        int i = 0;
        while (i < size) {
            eliminateEpsilon(grammar);
            i++;
        }

        //消除所有除了s1上的空产生式
        for (int k = 0; k < productions.size(); k++) {
            List<symbol> rhs = productions.get(k).getRhs();
            String lhs = productions.get(k).getLhs();
            String newLhs = grammar.getStartSymbol() + "1";
            for (int m = 0; m < rhs.size(); m++){
                symbol symbol = rhs.get(m);
                if (symbol.getName().equals("Ƹ") && (!lhs.equals(newLhs))){
                    productions.remove(k);
                }
            }
        }

    }

    public void eliminateEpsilon(Grammar grammar) {
        List<nonterm> nonterminalSymbols = grammar.getNonterminalSymbols();
        List<production> productions = grammar.getProductions();

        //寻找Epsilon
        //有Epsilon的表达式的非
        List<String> lhs = new ArrayList<>();
        for (production production : productions) {
            List<symbol> rhs = production.getRhs();
            for (symbol symbol : rhs) {
                if (symbol.getName().equals("Ƹ")) {
                    lhs.add(production.getLhs());
                }
            }
        }
        //替换所包含该非终止符的产生式
//        System.out.println(lhs);

        int size = nonterminalSymbols.size();
        for (int i = 0; i < size; i++) {
            nonterm nontermI = nonterminalSymbols.get(i);
            List<production> productionsI = new ArrayList<>();
            for (production production : productions) {
                if (production.getLhs().equals(nontermI.getName())) {
                    productionsI.add(production);
                }
            }
//            System.out.println(productionsI);
            productions.removeAll(productionsI);
            for (String name : lhs) {
                for (int j = 0; j < productionsI.size(); j++) {
                    List<symbol> rhs = productionsI.get(j).getRhs();
//                    System.out.println(rhs);
                    for (int m = 0; m < rhs.size(); m++) {
                        symbol symbol = rhs.get(m);
                        if (symbol.getName().equals(name)) {
                            //找到可以被替换的非终结符
                            production p = new production();
                            List<symbol> rhsTemp = new ArrayList<>(rhs);
                            rhsTemp.remove(m);
                            p.setLhs(nontermI.getName());
                            p.getRhs().addAll(rhsTemp);
                            //增加被Epsilon替代的非终结符的产生式
                            //重复项不添加
                            int flag2 = 0;
                            for (int z = 0; z < productionsI.size(); z++) {
                                List<symbol> rhsZ = productionsI.get(z).getRhs();
                                if (p.getRhs().equals(rhsZ)) {
                                    flag2++;
                                }
                            }
                            if (flag2 == 0) {
                                productionsI.add(p);
                            }
                        }
                        //删除空产生式
                        if (symbol.getName().equals("Ƹ")) {
                            //如果空产生式在StartSymbol上，加一条新的产生式
                            if (productionsI.get(j).getLhs().equals(grammar.getStartSymbol())) {
                                production addP1 = new production();
                                production addP2 = new production();
                                String newLhs = grammar.getStartSymbol() + "1";
                                nonterminalSymbols.add(new nonterm(newLhs));
                                addP1.setLhs(newLhs);
                                addP2.setLhs(newLhs);
                                addP1.getRhs().add(new symbol(grammar.getStartSymbol(), "nonterm"));
                                addP2.getRhs().add(symbol);
                                productions.add(addP1);
                                productions.add(addP2);
                            }
                            productionsI.remove(j);
                            j--;
                        }
                    }
                }
            }
            //更新
            productions.addAll(productionsI);
//            System.out.println(productions);
        }
        for (int k = 0; k < productions.size(); k++) {
            List<symbol> rhs = productions.get(k).getRhs();
            if (rhs.size() == 0) {
                rhs.add(new symbol("Ƹ", "term"));
            }
        }
    }
}
