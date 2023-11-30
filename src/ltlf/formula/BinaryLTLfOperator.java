package ltlf.formula;

public class BinaryLTLfOperator extends LTLfOperator {
    public BinaryLTLfOperator(String symbol) {
        super(symbol);
    }

    @Override
    public String format(String... children) {
        return "(" + children[0] + " " + symbol + " " + children[1] + ")";
    }
}
