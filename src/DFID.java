import java.util.*;

public class DFID implements Algorithm{

   //////////////////////////// DATA ////////////////////////

   private int nodeNum = 0;    // number of nodes created

   ////////////////////////// METHODS ////////////////////////

   @Override
   public State solve(String[][] initialState, String[][] goalState, boolean withOpen) {
      for (int depth = 1;  depth < 15 ;depth++) {
         State result = limitedDFS(new State(initialState, 0, ""), goalState, depth, withOpen, new HashSet<>());
         if (result != null) {       //if result != cutoff
            result.setNum(nodeNum);
            return result;
         }
      }
      State noSolution = new State(new String[3][3], -1, "no path");    // create a state with -1 cost (no solution)
      noSolution.setNum(nodeNum);    // set the number of nodes created
      return noSolution;    // no solution found
   }

   public State limitedDFS(State current, String[][] goalState, int depth, boolean withOpen, Set<State> path) {
      if (withOpen) {
         System.out.println("Open list: " + current.toString());
      }
      if (Arrays.deepEquals(current.getBoard(), goalState)) {
         return current;
      }
      if (depth == 0) {
         return null;
      }

      path.add(current);

      for (State nextState : getNextOptionalStates(current)) {
         if (!path.contains(nextState)) {    // loop avoidance
            nodeNum++;
            if (depth > 0){
               State result = limitedDFS(nextState, goalState, depth - 1, withOpen, path);
               if (result != null) {
                  return result;
               }
            }
         }
      }
      path.remove(current);
      return null;
   }
   /*
    * Get the next optional states from the current state
    * @param current - the current state
    * @return a list of the next optional states
    */
   public List<State> getNextOptionalStates(State current) {
      List<State> optionalStates = new ArrayList<>();
      String[][] board = current.getBoard();

      for (int i = 0; i < board.length; i++){
         for (int j = 0; j < board.length; j++){
            if (!Objects.equals(board[i][j], "_")){
               for (int[] direction : new int[][]{{-1, 0}, {1, 0}, {0, -1}, {0, 1}}){      // for each direction
                  int x =( j + direction[0]  + board.length) % board.length;
                  int y = (i + direction[1] + board.length) % board.length;

                  if (board[x][y].equals("_")) {       // if the empty cell is in the direction -> valid move
                     String[][] newBoard =  new String[3][3];
                     for (int k = 0; k < board.length; k++){
                        newBoard[k] = board[k].clone();
                     }
                     // swap the empty cell with the current cell -> move the current cell to the empty cell
                     newBoard[x][y] = newBoard[i][j];
                     newBoard[i][j] = "_";

                     String move = String.format("(%d,%d):%s:(%d,%d)--", i+1, j+1, board[i][j], x+1, y+1);
                     int moveCost =  getCost(board[i][j]);
                     optionalStates.add(new State(newBoard, current.totalCost() + moveCost, current.getPath() + move));
                     //nodeNum++;
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