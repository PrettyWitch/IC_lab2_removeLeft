import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yuhan
 * @date 03.04.2021 - 11:37
 * @purpose 消除左递归
 */
public class RemoveLeft {

//    public void JsonTest() throws IOException {
//        String path = "/input.json";
//        InputStream config = getClass().getResourceAsStream(path);
//        JSONObject json = JSON.parseObject(config, JSONObject.class);
//        System.out.println("json: "+json);
//        String text = JSONObject.parseObject(String.valueOf(json)).get("grammar").toString().replace("-", "");
//        System.out.println("text: "+text);
//    }

    //读取json文件，并进行解析
    public Grammar readJson(String path) throws Exception {
//        InputStream inputStream = new FileInputStream(path);
        InputStream inputStream = getClass().getResourceAsStream(path);
        String text = IOUtils.toString(inputStream, "UTF-8");
        text = JSONObject.parseObject(text).get("grammar").toString().replace("-", "");
//        System.out.println("text: " + text);
        JSONObject g = JSONObject.parseObject(text);
//        System.out.println("g: " + g);
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
                    case "IDENT":
                        System.out.print("a ");
                        break;
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

    /*
    输入：不含循环推导（即形如A + A的推导）和ε-产生式的文法G
    输出：等价的无左递归文法
    方法：
    按照某个顺序将非终结符号排序为A1，A2，… ，An  .
    for ( 从1到n的每个i ) {
         for ( 从1到i -1的每个i ) {
                 将每个形如Ai → Aj γ的产生式替换为产生式组 Ai → δ1 γ∣δ2 γ∣…∣δk γ ，
                         其中Aj  → δ1∣δ2∣… ∣δk ，是所有的Aj 产生式
         }
         消除Ai 产生式之间的立即左递归
    }
     */
    public void leftRecursionRemoval(Grammar g) {
        List<nonterm> nonterminalSymbols = g.getNonterminalSymbols();
        List<production> productions = g.getProductions();
        int size = nonterminalSymbols.size();
        for (int i = 0; i < size; i++) {
            nonterm nontermI = nonterminalSymbols.get(i);
            List<production> productionsI = new ArrayList<>();
            for (production production : productions) {
                if (production.getLhs().equals(nontermI.getName())) {
                    productionsI.add(production);
                }
            }
//            System.out.println(i + "i: " + productionsI);
            //消除间接左递归
            for (int j = 1; j < i - 1; j++) {
                nonterm nontermJ = nonterminalSymbols.get(j);
                List<production> productionsJ = new ArrayList<>();
                for (production production : productions) {
                    //得到j的表达式
                    if (production.getLhs().equals(nontermJ.getName())) {
                        productionsJ.add(production);
                    }
                }
                //找productionsI中是否有以j开头的表达式
                for (production production : productionsI) {
                    List<symbol> rhs = production.getRhs();
                    for (int m = 0; m < rhs.size(); m++) {
                        symbol symbol = rhs.get(m);
                        //找到了间接左递归,替换
                        if (symbol.getName().equals(nontermJ.getName())) {
                            for (production pj : productionsJ) {
                                //如果j非终结符在首位
                                if (m == 0) {
                                    List<symbol> newRhs = pj.getRhs();
                                    rhs.remove(m);
                                    newRhs.addAll(rhs);
                                    rhs.clear();
                                    rhs.addAll(newRhs);
                                } else if (m == rhs.size() - 1) {//如果j非终结符在末位
                                    List<symbol> newRhs = pj.getRhs();
                                    rhs.remove(m);
                                    rhs.addAll(newRhs);
                                } else {//如果j非终结符在中间
                                    List<symbol> newRhs = pj.getRhs();
                                    newRhs.clear();
                                    newRhs.addAll(rhs.subList(0, m));
                                    newRhs.addAll(pj.getRhs());
                                    newRhs.addAll(m + 1, rhs);
                                    rhs.clear();
                                    rhs.addAll(newRhs);
                                }
                            }

                        }
                    }
                }
            }
            //消除直接左递归
            int index = 1;
            boolean flag = true;
            while (flag) {
                for (int j = 0; j < productionsI.size(); j++) {
                    List<symbol> rhs = productionsI.get(j).getRhs();
                    if (rhs.get(0).getName().equals(nontermI.getName())) {
                        //存在直接左递归
                        productions.removeAll(productionsI);
                        productionsI.remove(j);
                        production addP = new production();
                        String newLhs = nontermI.getName() + "" + index;
                        index++;
                        addP.setLhs(newLhs);
                        nonterminalSymbols.add(new nonterm(newLhs));
                        //从1开始
                        for (int n = 1; n < rhs.size(); n++) {
                            addP.getRhs().add(rhs.get(n));
                        }
                        addP.getRhs().add(rhs.get(0));
                        addP.getRhs().get(rhs.size() - 1).setName(newLhs);
//                        System.out.println(addP);
                        //在之前的productionsI每一个rhs后面加上一个addP的name
                        for (production production : productionsI) {
                            production.getRhs().add(addP.getRhs().get(rhs.size() - 1));
                        }
                        productionsI.add(addP);
                        //如果productionsI中有不带有name的production，加空推导
                        symbol emptySymbol = new symbol("Ƹ", "term");
                        production emptyP = new production();
                        emptyP.setLhs(newLhs);
                        emptyP.getRhs().add(emptySymbol);
                        productionsI.add(emptyP);
                        productions.addAll(productionsI);
//                        System.out.println("productionsI: "+productionsI);
                        break;
                    }else{
                        productions.removeAll(productionsI);
                        productions.addAll(productionsI);
                    }
                }
                //将下标复原
                index = 1;
                flag = false;
            }
        }
    }


    public void saveGrammar(Grammar grammar){
        JSONObject jsonObject = (JSONObject) JSON.toJSON(grammar);
        try {
            OutputStreamWriter output = new OutputStreamWriter(new FileOutputStream("src\\main\\resources\\output.json"), StandardCharsets.UTF_8);
            output.write(jsonObject.toJSONString());
            output.flush();
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        System.out.println("\n"+jsonObject.toJSONString());

        //DisableCircularReferenceDetect消除对同一对象循环引用的问题，默认为false
//        String s = JSON.toJSONString(grammar, SerializerFeature.DisableCircularReferenceDetect);
////        s = JsonFormatTool
//        File file = new File("src\\main\\resources\\output.json");
//        try {
//            FileUtils.writeStringToFile(file, s);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }
}

