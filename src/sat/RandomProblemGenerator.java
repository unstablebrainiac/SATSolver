package sat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomProblemGenerator {
    private final Random random;

    public RandomProblemGenerator(long seed) {
        random = new Random(seed);
    }

    public CNFProblem generate(int N, int L, int K) {
        CNFProblem problem = new CNFProblem();
        for (int i = 0; i < L; i++) {
            problem.addClause(generateClause(N, K));
        }
        return problem;
    }

    private Clause generateClause(int N, int K) {
        List<Integer> propositions = new ArrayList<>();
        for (int i = 0; i < K; i++) {
            int proposition = random.nextInt(N) + 1;
            if (random.nextBoolean()) {
                proposition = -proposition;
            }
            propositions.add(proposition);
        }
        return new Clause(propositions);
    }
}
