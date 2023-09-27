package sat;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CNFSolution {
    private boolean isSatisfiable;
    private final Map<Integer, Boolean> assignment;
    private int numDPLLSteps = 0;
    private boolean isTimedOut = false;

    public CNFSolution() {
        this.assignment = new HashMap<>();
        this.isSatisfiable = true;
    }

    public CNFSolution(Map<Integer, Boolean> assignment, boolean isSatisfiable) {
        this.assignment = assignment;
        this.isSatisfiable = isSatisfiable;
    }

    public CNFSolution(CNFSolution solution) {
        this.assignment = new HashMap<>(solution.getAssignment());
        this.isSatisfiable = solution.isSatisfiable();
        this.numDPLLSteps = solution.getNumDPLLSteps();
    }

    public boolean isSatisfiable() {
        return isSatisfiable;
    }

    public Map<Integer, Boolean> getAssignment() {
        return assignment;
    }

    public Boolean getAssignment(Integer proposition) {
        if (proposition < 0) {
            Boolean opposite = assignment.get(-proposition);
            if (opposite == null) {
                return null;
            }
            return !opposite;
        }
        return assignment.get(proposition);
    }

    public boolean checkAndSetAssignment(Integer proposition, boolean isPositive) {
        if (proposition < 0) {
            throw new IllegalArgumentException("Proposition must be positive");
        }
        if (assignment.containsKey(proposition)) {
            return assignment.get(proposition) == isPositive;
        } else {
            assignment.put(proposition, isPositive);
            return true;
        }
    }

    public void setSatisfiable(boolean isSatisfiable) {
        this.isSatisfiable = isSatisfiable;
    }

    public void setAssignment(Map<Integer, Boolean> assignment) {
        this.assignment.clear();
        this.assignment.putAll(assignment);
    }

    public void incrementNumDPLLSteps() {
        numDPLLSteps++;
    }

    public int getNumDPLLSteps() {
        return numDPLLSteps;
    }

    public void setNumDPLLSteps(int numDPLLSteps) {
        this.numDPLLSteps = numDPLLSteps;
    }

    public boolean isTimedOut() {
        return isTimedOut;
    }

    public void setTimedOut(boolean isTimedOut) {
        this.isTimedOut = isTimedOut;
    }

    @Override
    public String toString() {
//        return isSatisfiable ? assignment.toString() : "UNSATISFIABLE";
        if (isSatisfiable) {
            return assignment
                    .entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(entry -> (entry.getValue() ? "" : "-") + entry.getKey())
                    .collect(Collectors.joining(" "));
        } else {
            return "UNSATISFIABLE";
        }
    }
}
