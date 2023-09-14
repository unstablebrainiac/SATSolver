package sat;

import com.google.common.collect.Sets;

import java.util.HashSet;
import java.util.Optional;

public class TwoClauseDPLLSolver extends DPLLSolver {

    @Override
    protected Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution) {
        // TODO Choose the proposition that appears in the most two clauses
        Optional<Clause> maybeTwoClause = problem.getClauses()
                .stream()
                .filter(clause -> clause.size() == 2)
                .findAny();
        if (maybeTwoClause.isPresent()) {
            Clause twoClause = maybeTwoClause.get();
            return Optional.of(Math.abs(twoClause.getProposition(0)));
        } else {
            return Sets.difference(
                            new HashSet<>(problem.getPropositions()),
                            solution.getAssignment().keySet()
                    )
                    .stream()
                    .findAny();
        }
    }
}
