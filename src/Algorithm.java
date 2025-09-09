import java.util.List;

public interface Algorithm {
    State solve(String[][] initialState, String[][] goalState, boolean withOpen);

    List<State> getNextOptionalStates(State current);

    int getCost(String ball);
}