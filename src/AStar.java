import java.util.*;

public class AStar implements Algorithm {
   //////////////////////////// DATA ////////////////////////
   private int nodeNum = 0; // Number of nodes created

   ////////////////////////// METHODS ////////////////////////

   @Override
   public State solve(String[][] initialState, String[][] goalState, boolean withOpen) {
      PriorityQueue<State> openList = new PriorityQueue<>(Comparator.comparingInt(
              state -> state.totalCost() + heuristic(state.getBoard(), goalState)));
      Map<State, Integer> closedList = new HashMap<>();

      State start = new State(initialState, 0, ""); // Create start state
      openList.add(start); // Add start state to open list

      while (!openList.isEmpty()) {
         if (withOpen) { // Print open list if required
            System.out.println("Open list: " + openList.toString());
         }

         State current = openList.poll(); // Get state with lowest f(n) = g(n) + h(n)

         if (Arrays.deepEquals(current.getBoard(), goalState)) { // Check if it's the goal state
            current.setNum(nodeNum); // Set the number of nodes created
            return current; // Solution found
         }

         closedList.put(current, current.totalCost());

         for (State nextState : getNextOptionalStates(current)) { // For each neighbor
            int nextCost = nextState.totalCost();
            if (!closedList.containsKey(nextState) || nextCost < closedList.get(nextState)) {
               openList.add(nextState);
               closedList.put(nextState, nextCost);
            }
         }
      }

      State noSolution = new State(new String[3][3], -1, "no path"); // No solution
      noSolution.setNum(nodeNum); // Set node count
      return noSolution;
   }

   @Override
   public List<State> getNextOptionalStates(State current) {
      List<State> optionalStates = new ArrayList<>();
      String[][] board = current.getBoard();

      for (int i = 0; i < board.length; i++) {
         for (int j = 0; j < board.length; j++) {
            if (!Objects.equals(board[i][j], "_") && !Objects.equals(board[i][j], "X")) { // Valid ball
               for (int[] direction : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) { // Move directions
                  int x = (j + direction[1] + board.length) % board.length;
                  int y = (i + direction[0] + board.length) % board.length;

                  if (board[y][x].equals("_")) { // Valid move
                     String[][] newBoard = new String[3][3];
                     for (int k = 0; k < board.length; k++) {
                        newBoard[k] = board[k].clone();
                     }

                     // Perform the swap
                     newBoard[y][x] = newBoard[i][j];
                     newBoard[i][j] = "_";

                     String move = String.format("(%d,%d):%s:(%d,%d)--", i + 1, j + 1, board[i][j], y + 1, x + 1);
                     int moveCost = getCost(board[i][j]);
                     optionalStates.add(new State(newBoard, current.totalCost() + moveCost, current.getPath() + move));
                     nodeNum++;
                  }
               }
            }
         }
      }

      return optionalStates;
   }

   @Override
   public int getCost(String ball) {
      return switch (ball) {
         case "B" -> 1;
         case "G" -> 3;
         case "R" -> 10;
         default -> 0; // Empty cell or wall
      };
   }

   //////////////////////////// HEURISTIC FUNCTION ////////////////////////

   public int heuristic(String[][] currentBoard, String[][] goalBoard) {
      int h = 0;
      String[] colors = {"B", "G", "R"};

      for (String color : colors) { // For each color
         int[] currentLocation = searchSpecificColor(currentBoard, color); // Get current locations of balls
         int[] goalLocation = searchSpecificColor(goalBoard, color); // Get goal locations of balls
         int moveCosts = computeMoves(currentLocation, goalLocation); // Compute the moves needed
         h += moveCosts * getCost(color); // Add the cost to the heuristic value
      }

      return h;
   }

   public int[] searchSpecificColor(String[][] board, String color) {
      int found = 0;
      int[] locations = new int[4]; // {x1, y1, x2, y2} -> (x1,y1) is first, (x2,y2) is second
      for (int i = 0; i < board.length; i++) {
         for (int j = 0; j < board.length; j++) {
            if (board[i][j].equals(color) && found < 2) {
               locations[found * 2] = i; // x-coordinate
               locations[found * 2 + 1] = j; // y-coordinate
               found++;
            }
         }
      }
      return locations;
   }

   public int computeMoves(int[] currentLocation, int[] goalLocation) {
      int boardSize = 3; // Assuming 3x3 board; adjust if dynamic size is used

      // Horizontal (x) and vertical (y) moves considering wrapping
      int firstToFirst = Math.min(Math.abs(currentLocation[0] - goalLocation[0]),
              boardSize - Math.abs(currentLocation[0] - goalLocation[0]))
              + Math.min(Math.abs(currentLocation[1] - goalLocation[1]),
              boardSize - Math.abs(currentLocation[1] - goalLocation[1]));

      int secondToSecond = Math.min(Math.abs(currentLocation[2] - goalLocation[2]),
              boardSize - Math.abs(currentLocation[2] - goalLocation[2]))
              + Math.min(Math.abs(currentLocation[3] - goalLocation[3]),
              boardSize - Math.abs(currentLocation[3] - goalLocation[3]));

      int firstToSecond = Math.min(Math.abs(currentLocation[0] - goalLocation[2]),
              boardSize - Math.abs(currentLocation[0] - goalLocation[2]))
              + Math.min(Math.abs(currentLocation[1] - goalLocation[3]),
              boardSize - Math.abs(currentLocation[1] - goalLocation[3]));

      int secondToFirst = Math.min(Math.abs(currentLocation[2] - goalLocation[0]),
              boardSize - Math.abs(currentLocation[2] - goalLocation[0]))
              + Math.min(Math.abs(currentLocation[3] - goalLocation[1]),
              boardSize - Math.abs(currentLocation[3] - goalLocation[1]));

      return Math.min(firstToFirst + secondToSecond, firstToSecond + secondToFirst);
   }
}
