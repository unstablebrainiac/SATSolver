package ltlf.formula;

public abstract class LTLfOperator {
    protected final String symbol;

    protected LTLfOperator(String symbol) {
        this.symbol = symbol;
    }

    public abstract String format(String... children);
}
