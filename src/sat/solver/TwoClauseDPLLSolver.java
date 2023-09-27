package sat.solver;

import sat.CNFProblem;
import sat.CNFSolution;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TwoClauseDPLLSolver extends DPLLSolver {

    private final Random random;

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
                                .flatMap(Collection::stream)
                                .filter(clause -> clause.size() == 2)
                                .count()
                ));

        if (propositionToTwoClauseCount.isEmpty()) {
            return Optional.empty();
        }
        Long max = propositionToTwoClauseCount.values().stream().max(Comparator.naturalOrder()).get();
        if (max == 0) {
            int unassignedPropositionsCount = problem.getPropositions().size() - solution.getAssignment().size();
            int index = random.nextInt(unassignedPropositionsCount);
            return problem.getPropositions()
                    .stream()
                    .filter(proposition -> !solution.getAssignment().containsKey(proposition))
                    .skip(index)
                    .findFirst();
        }
        List<Integer> propositionsWithMaxTwoClauses = propositionToTwoClauseCount
                .entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(max))
                .map(Map.Entry::getKey)
                .toList();

        int index = random.nextInt(propositionsWithMaxTwoClauses.size());
        return Optional.of(propositionsWithMaxTwoClauses.get(index));
    }

    @Override
    protected boolean chooseFirstAssignment(Integer proposition, CNFProblem problem, CNFSolution solution) {
        return random.nextBoolean();
    }
}
