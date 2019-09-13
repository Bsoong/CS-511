import java.io.*;
import java.util.*;

public class TextSwap {

    private static String readFile(String filename) throws Exception {
        String line;
        StringBuilder buffer = new StringBuilder();
        File file = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(file));
        while ((line = br.readLine()) != null) {
            buffer.append(line);
        }
        br.close();
        return buffer.toString();
    }

    private static Interval[] getIntervals(int numChunks, int chunkSize) {
      int begin = 0;
      int end = chunkSize -1;
      Interval[] arr = new Interval[numChunks];
      for (int i = 0; i < numChunks; i++)
            arr[i] = new Interval(i*chunkSize, (i*chunkSize) + end);
        return arr;
  }

    private static List<Character> getLabels(int numChunks) {
        Scanner scanner = new Scanner(System.in);
        List<Character> labels = new ArrayList<Character>();
        int endChar = numChunks == 0 ? 'a' : 'a' + numChunks - 1;
        System.out.printf("Input %d character(s) (\'%c\' - \'%c\') for the pattern.\n", numChunks, 'a', endChar);
        for (int i = 0; i < numChunks; i++) {
            labels.add(scanner.next().charAt(0));
        }
        scanner.close();
        // System.out.println(labels);
        return labels;
    }

    private static char[] runSwapper(String content, int chunkSize, int numChunks) {
      List<Character> labels = getLabels(numChunks);
      Interval[] intervals = getIntervals(numChunks, chunkSize);
      char[] buffer = new char[content.length()];
      boolean temp = false;
      List<Thread> tList = new ArrayList<Thread>();
      for(int i = 0; i < labels.size(); i++){
          Swapper t1 = new Swapper(intervals[labels.get(i)-'a'], content, buffer, chunkSize*i);
          Thread t2 = new Thread(t1);
          t2.start();
          tList.add(t2);
        }
      while (!temp){
          for (int i = 0; i < tList.size(); i++){
              if(!tList.get(i).isAlive()){
                  temp = true;
            } else{
                  temp = false;
                }
              }
            }
            return buffer;
          }
    private static void writeToFile(String contents, int chunkSize, int numChunks) throws Exception {
        char[] buff = runSwapper(contents, chunkSize, contents.length() / chunkSize);
        PrintWriter writer = new PrintWriter("output.txt", "UTF-8");
        writer.print(buff);
        writer.close();
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java TextSwap <chunk size> <filename>");
            return;
        }
        String contents = "";
        int chunkSize = Integer.parseInt(args[0]);
        try {
            contents = readFile(args[1]);
            writeToFile(contents, chunkSize, contents.length() / chunkSize);
        } catch (Exception e) {
            System.out.println("Error with IO.");
            return;
        }
    }
}
