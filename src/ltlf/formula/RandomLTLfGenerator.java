package ltlf.formula;

import java.util.List;
import java.util.Random;

public class RandomLTLfGenerator {
    private final Random random;
    private final int size;
    private final int propositions;
    private final List<UnaryLTLfOperator> unaryOperators = List.of(
            new UnaryLTLfOperator("G"),
            new UnaryLTLfOperator("F"),
            new UnaryLTLfOperator("X"),
            new UnaryLTLfOperator("!")
    );

    private final List<BinaryLTLfOperator> binaryOperators = List.of(
            new BinaryLTLfOperator("&"),
            new BinaryLTLfOperator("|"),
            new BinaryLTLfOperator("->"),
            new BinaryLTLfOperator("U")
    );

    public RandomLTLfGenerator(long seed, int size, int propositions) {
        this.random = new Random(seed);
        this.size = size;
        this.propositions = propositions;
    }

    public String generate() {
        return generate(size);
    }

    private String generate(int size) {
        if (size == 1) {
            return generateProposition();
        }
        if (size == 2) {
            return generateUnary(size);
        }
        if (random.nextBoolean()) {
            return generateUnary(size);
        } else {
            return generateBinary(size);
        }
    }

    private String generateProposition() {
        return "p" + random.nextInt(propositions);
    }

    private String generateUnary(int size) {
        UnaryLTLfOperator operator = unaryOperators.get(random.nextInt(unaryOperators.size()));
        return operator.format(generate(size - 1));
    }

    private String generateBinary(int size) {
        BinaryLTLfOperator operator = binaryOperators.get(random.nextInt(binaryOperators.size()));
        int leftSize = random.nextInt(size - 2) + 1;
        int rightSize = size - leftSize - 1;
        return operator.format(generate(leftSize), generate(rightSize));
    }
}
