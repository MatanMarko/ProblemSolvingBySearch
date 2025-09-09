import java.util.*;

public class BFS implements Algorithm {
   //////////////////////////// DATA ////////////////////////
   private int nodeNum = 0;    // number of nodes created

   ////////////////////////// METHODS ////////////////////////

   /*
    * Solve the problem using BFS algorithm
    * @param initialState - the initial state of the board
    * @param goalState - the goal state of the board
    * @param withOpen - whether to print the open list
    * @return the last state in the path to the solution AKA the goal state (if found) or null (if not found)
    */
   @Override
   public State solve(String[][] initialState, String[][] goalState, boolean withOpen) {
      Queue<State> q = new LinkedList<>();        // open list
      Hashtable<State, Boolean> visited = new Hashtable<>();    // closed list

      State start = new State(initialState, 0, ""); // create start state
      q.add(start);                                   // add start state to open list
      visited.put(start, true);                      // add start state to closed list

      while (!q.isEmpty()) {
         if (withOpen) {     // if we need to print the open list
            System.out.println("Open list: " + q.toString());
         }
         State current = q.poll();   // get the first state in the open list
         if (Arrays.deepEquals(current.getBoard(), goalState)) {     // if the current state is the goal state
            current.setNum(nodeNum);    // set the number of nodes created
            return current;     // return the current state (solution found)
         }

         for (State nextState : getNextOptionalStates(current)) {  // for each neighbor of the current state
            if (!visited.containsKey(nextState)) {       // if the neighbor is not in the closed list
               q.add(nextState);                        // add the neighbor to the open list
               visited.put(nextState, true);           // add the neighbor to the closed list
            }
         }
      }
      State noSolution = new State(new String[3][3], -1, "no path");    // create a state with -1 cost (no solution)
      noSolution.setNum(nodeNum);    // set the number of nodes created
      return noSolution;    // no solution found
   }

   /*
    * Get the next optional states from the current state
    * @param current - the current state
    * @return a list of the next optional states
    */
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
         default -> 0;  // empty cell
      };

   }
}