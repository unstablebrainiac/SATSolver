package sat;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CNFProblem {

    private final List<Integer> propositions;
    private final List<Clause> clauses;
    private final Map<Integer, List<Clause>> propositionToClauses;

    public CNFProblem() {
        this.propositions = new ArrayList<>();
        this.clauses = new ArrayList<>();
        this.propositionToClauses = new HashMap<>();
    }

    public CNFProblem(List<Clause> clauses) {
        this.propositions = clauses.stream()
                .flatMap(clause -> clause.getPropositions().stream())
                .distinct()
                .map(Math::abs)
                .toList();
        this.clauses = clauses;
        this.propositionToClauses = generatePropositionToClausesMap();
    }

    public CNFProblem(CNFProblem problem) {
        this.propositions = new ArrayList<>(problem.getPropositions());
        this.clauses = problem.getClauses()
                .stream()
                .map(Clause::new)
                .collect(Collectors.toList());
        this.propositionToClauses = generatePropositionToClausesMap();
    }

    private Map<Integer, List<Clause>> generatePropositionToClausesMap() {
        return clauses
                .stream()
                .map(Clause::getPropositions)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        proposition -> clauses
                                .stream()
                                .filter(clause -> clause.contains(proposition))
                                .collect(Collectors.toList())
                ));
    }

    public List<Integer> getPropositions() {
        return propositions;
    }

    public List<Clause> getClauses() {
        return clauses;
    }

    public void addClause(Clause clause) {
        clauses.add(clause);
        clause.getPropositions()
                .stream()
                .map(Math::abs)
                .forEach(proposition -> {
                    if (!propositions.contains(proposition)) {
                        propositions.add(proposition);
                    }
                });
        clause.getPropositions()
                .forEach(proposition -> propositionToClauses.computeIfAbsent(proposition, key -> new ArrayList<>()).add(clause));
    }

    public List<Clause> getClausesContainingProposition(Integer proposition) {
        return Optional.ofNullable(propositionToClauses.get(proposition)).orElse(new ArrayList<>());
    }

    @Override
    public String toString() {
        return clauses.stream().map(Clause::toString).collect(Collectors.joining(" AND "));
    }
}
