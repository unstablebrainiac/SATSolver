package ltlf.formula;

public class UnaryLTLfOperator extends LTLfOperator {
    public UnaryLTLfOperator(String symbol) {
        super(symbol);
    }

    @Override
    public String format(String... children) {
        return symbol + "(" + children[0] + ")";
    }
}
