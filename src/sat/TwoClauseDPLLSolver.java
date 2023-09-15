package sat;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwoClauseDPLLSolver extends DPLLSolver {

    Random random;

    public TwoClauseDPLLSolver(long seed) {
        random = new Random(seed);
    }

    @Override
    protected Optional<Integer> choosePropositionToAssign(CNFProblem problem, CNFSolution solution) {
        Map<Integer, Long> propositionToTwoClauseCount = problem.getPropositions()
                .stream()
                .filter(proposition -> !solution.getAssignment().containsKey(proposition))
                .collect(Collectors.toMap(
                        Function.identity(),
                        proposition -> Stream.of(
                                        problem.getClausesContainingProposition(proposition),
                                        problem.getClausesContainingProposition(-proposition)
                                )
                                .filter(clause -> clause.size() == 2)
                                .count()
                ));

        if (propositionToTwoClauseCount.isEmpty()) {
            int unassignedPropositionsCount = problem.getPropositions().size() - solution.getAssignment().size();
            if (unassignedPropositionsCount == 0) {
                return Optional.empty();
            }
            int index = random.nextInt(unassignedPropositionsCount);
            return problem.getPropositions()
                    .stream()
                    .filter(proposition -> !solution.getAssignment().containsKey(proposition))
                    .skip(index)
                    .findFirst();
        }
        Long max = propositionToTwoClauseCount.values().stream().max(Comparator.naturalOrder()).get();
        List<Integer> propositionsWithMaxTwoClauses = propositionToTwoClauseCount
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(max))
                .map(Map.Entry::getKey)
                .toList();

        int index = random.nextInt(propositionsWithMaxTwoClauses.size());
        return Optional.of(propositionsWithMaxTwoClauses.get(index));
    }
}
