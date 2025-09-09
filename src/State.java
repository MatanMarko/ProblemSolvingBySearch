import java.util.Arrays;
import java.util.Objects;

public class State {
   private String[][] board;  // The current state of the board
   private int cost;          // The total cost to reach this state
   private String path;       // The path taken to reach this state
   private int num;           // Number of nodes visited or processed

   ////////////// Constructor///////////////
   public State(String[][] board, int cost, String path) {
      this.board = board;
      this.cost = cost;
      this.path = path;
   }

   ///////////////// Getters///////////////
   public String[][] getBoard() {
      return board;
   }

   public int totalCost() {
      return cost;
   }

   public String getPath() {
      return path;
   }

   public int getNum() {
      return num;
   }

   ///////////// Setters///////////////
   public void setNum(int num) {
      this.num = num;
   }

   //////////////////////////// OVERRIDDEN METHODS ////////////////////////
   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null || getClass() != obj.getClass()) return false;
      State state = (State) obj;
      return Arrays.deepEquals(board, state.board);
   }


   @Override
   public int hashCode() {
      return Arrays.deepHashCode(board);
   }


   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      for (String[] row : board) {
         for (String cell : row) {
            sb.append(cell).append(" ");
         }
         sb.append("\n");
      }
      sb.append("Cost: ").append(cost).append("\n");
      sb.append("Path: ").append(path).append("\n");
      return sb.toString();
   }


}
