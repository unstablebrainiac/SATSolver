package sat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Clause {
    List<Integer> propositions;

    public Clause(List<Integer> propositions) {
        this.propositions = propositions;
    }

    public Clause(Clause clause) {
        this.propositions = new ArrayList<>(clause.getPropositions());
    }

    public List<Integer> getPropositions() {
        return propositions;
    }

    public Integer getProposition(int index) {
        return propositions.get(index);
    }

    public int size() {
        return propositions.size();
    }

    public boolean contains(Integer proposition) {
        return propositions.contains(proposition);
    }

    public boolean isEmpty() {
        return propositions.isEmpty();
    }

    public boolean isSatisfiedBy(Map<Integer, Boolean> assignment) {
        for (Integer proposition : propositions) {
            if (proposition > 0) {
                if (assignment.containsKey(proposition) && assignment.get(proposition)) {
                    return true;
                }
            } else {
                if (assignment.containsKey(-proposition) && !assignment.get(-proposition)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "(" + propositions.stream().map(String::valueOf).collect(Collectors.joining(" OR ")) + ")";
    }
}
