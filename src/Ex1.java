import java.io.*;
import java.util.*;

public class Ex1 {

   public static class algorithmFactory {
      public Algorithm getAlgorithm(String algorithm) {
         switch (algorithm) {
            case "BFS":
               return new BFS();
            case "DFID":
               return new DFID();
            case "A*":
               return new AStar();
            case "IDA*":
               return new IDAStar();
            case "DFBnB":
               return new DFBnB();
            default:
               return null;
         }
      }
   }

   public static void main(String[] args) {
      String algorithmType;
      boolean withTime;
      boolean withOpen;
      String[][] initialState = new String[3][3];
      String[][] goalState = new String[3][3];

      // Read input from file
      try (BufferedReader br = new BufferedReader(new FileReader("input.txt"))) {
         // Read the algorithm type
         algorithmType = br.readLine();

         // Determine if we should print the timeline
         withTime = br.readLine().equals("with time");

         // Determine if we should print the open list
         withOpen = br.readLine().equals("with open");

         // Read the initial state of the board
         for (int i = 0; i < 3; i++) {
            initialState[i] = br.readLine().split(",");
         }

         // Skip the "Goal state:" line
         br.readLine();

         // Read the goal state of the board
         for (int i = 0; i < 3; i++) {
            goalState[i] = br.readLine().split(",");
         }
      } catch (IOException e) {
         System.err.println("Error reading input file: " + e.getMessage());
         return;
      }

      // Create the appropriate algorithm
      algorithmFactory factory = new algorithmFactory();
      Algorithm algorithm = factory.getAlgorithm(algorithmType);

      if (algorithm == null) {
         System.err.println("Invalid algorithm type: " + algorithmType);
         return;
      }

      // Execute the algorithm
      long startTime = System.currentTimeMillis();
      State solution = algorithm.solve(initialState, goalState, withOpen);
      long endTime = System.currentTimeMillis();
      String solutionPath = solution.getPath();

      // Write output to file
      try (BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {
         if (solutionPath == "no path") {
            bw.write("no path\n");
            bw.write("Num: " + solution.getNum() + "\n");
            bw.write("Cost: inf\n");
         } else if (solution != null) {
            String solutionTrimmed = solutionPath.substring(0, solutionPath.length() - 2);
            bw.write(solutionTrimmed + "\n");
            bw.write("Num: " + solution.getNum() + "\n");
            bw.write("Cost: " + solution.totalCost() + "\n");
         }

         if (withTime) {
            bw.write((endTime - startTime) / 1000.0 + " seconds\n");
         }
      } catch (IOException e) {
         System.err.println("Error writing output file: " + e.getMessage());
      }
   }
}
