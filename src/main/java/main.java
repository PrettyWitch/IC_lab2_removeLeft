/**
 * @author yuhan
 * @date 03.04.2021 - 12:00
 * @purpose
 */
public class main {
    public static void main(String[] args) {
        Grammar grammar = null;
        RemoveLeft removeLeft = null;
        Grammar grammar2 = null;
        RemoveEpsilon removeEpsilon = null;
        try {
            removeLeft = new RemoveLeft();
            grammar = removeLeft.readJson("/input.json");

            removeEpsilon = new RemoveEpsilon();
            grammar2 = removeEpsilon.readJson("/input3.json");
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        removeLeft.showGrammar(grammar);
        removeLeft.leftRecursionRemoval(grammar);
        System.out.println("\n-----Left Recursion Removal-----");
        removeLeft.showGrammar(grammar);
//        removeLeft.saveGrammar(grammar);

        removeEpsilon.showGrammar(grammar2);
        System.out.println("\n-----eliminate Epsilon-----");
        removeEpsilon.eliminateEpsilon2(grammar2);
        removeEpsilon.showGrammar(grammar2);





    }
}
