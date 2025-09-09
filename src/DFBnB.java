import java.util.*;

public class DFBnB implements Algorithm {

   //////////////////////////// DATA ////////////////////////

   private int nodeNum = 0; // number of nodes created

   ////////////////////////// METHODS ////////////////////////

   @Override
   public State solve(String[][] initialState, String[][] goalState, boolean withOpen) {
      Stack<State> openList = new Stack<>();
      Set<State> currentBranch = new HashSet<>(); // the current branch
      State bestSolution = null;
      int c = 10; // create small value for c
      int threshold = heuristic(initialState, goalState) + c; // Set the threshold to the heuristic value of the initial state + c

      State start = new State(initialState, 0, ""); // create start state
      openList.push(start); // add start state to open list
      currentBranch.add(start); // add start state to current branch

      while (!openList.isEmpty()) {
         State current = openList.pop();
         currentBranch.remove(current);

         if (withOpen) { // if we need to print the open list
            System.out.println("Open list: " + openList.toString());
         }

         int f = current.totalCost() + heuristic(current.getBoard(), goalState); // f(n) = g(n) + h(n)

         if (f >= threshold) { // if f(n) >= threshold
            continue;
         }

         if (Arrays.deepEquals(current.getBoard(), goalState)) { // if the current state is the goal state
            bestSolution = current;
            threshold = f; // Update threshold to the cost of this solution
            continue;
         }

         List<State> nextStates = getNextOptionalStates(current);
         nextStates.sort(Comparator.comparingInt(
                 s -> s.totalCost() + heuristic(s.getBoard(), goalState))); // Sort by f(n)

         for (int i = nextStates.size() - 1; i >= 0; i--) {
            State nextState = nextStates.get(i);
            if (!currentBranch.contains(nextState)) { // Check for loop avoidance
               openList.push(nextState); // add the neighbor to the open list
               currentBranch.add(nextState); // add the neighbor to the current branch
               nodeNum++;
            }
         }
      }

      if (bestSolution == null) {
         State noSolution = new State(new String[3][3], -1, "no path"); // create a state with -1 cost (no solution)
         noSolution.setNum(nodeNum); // set the number of nodes created
         return noSolution; // no solution found
      }

      bestSolution.setNum(nodeNum);
      return bestSolution;
   }

   @Override
   public List<State> getNextOptionalStates(State current) {
      List<State> optionalStates = new ArrayList<>();
      String[][] board = current.getBoard();

      for (int i = 0; i < board.length; i++) {
         for (int j = 0; j < board.length; j++) {
            if (!Objects.equals(board[i][j], "_") && !Objects.equals(board[i][j], "X")) { // Only move valid balls
               for (int[] direction : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}) { // for each direction
                  int x = (j + direction[1] + board.length) % board.length;
                  int y = (i + direction[0] + board.length) % board.length;

                  if (board[y][x].equals("_")) { // if the empty cell is in the direction -> valid move
                     String[][] newBoard = new String[3][3];
                     for (int k = 0; k < board.length; k++) {
                        newBoard[k] = board[k].clone();
                     }

                     // Swap the empty cell with the current cell -> move the current cell to the empty cell
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
         default -> 0; // empty cell or wall
      };
   }

   //////////////////////////// HEURISTIC FUNCTION ////////////////////////

   /*
    * Heuristic function to calculate the heuristic value of the current state
    * For each ball in the current state, calculate the cost to move it to its place in the goal state
    * @param currentBoard - the current state's board representation
    * @param goalBoard - the goal state board representation
    * @return the heuristic value of the current state
    */
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

   /*
    * Helper function that searches for a specific color in the board
    */
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

   /*
    * Helper function that computes the moves needed to move a ball from its current location to its goal location
    */
   int computeMoves(int[] currentLocation, int[] goalLocation) {
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
