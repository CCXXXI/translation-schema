import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Java_TranslationSchemaAnalysis {

  public static void main(String[] args) throws IOException {
    final Solver solver = new Solver(new LineNumberReader(new InputStreamReader(System.in)));
    solver.solve();
  }

  static class Solver {

    final Map<String, Integer> intMap = new HashMap<>();
    final Map<String, Float> floatMap = new HashMap<>();
    private final LineNumberReader reader;
    boolean error = false;
    private Scanner scanner;
    private String currentSymbol;

    public Solver(LineNumberReader reader) {
      this.reader = reader;
    }

    private String nextSymbol() throws IOException {
      if (scanner != null && scanner.hasNext()) {
        currentSymbol = scanner.next();
      } else {
        String line = reader.readLine();
        if (line != null) {
          scanner = new Scanner(line);
          nextSymbol();
        } else {
          currentSymbol = null;
        }
      }
      return currentSymbol;
    }

    public void solve() throws IOException {
      nextSymbol();
      while (!currentSymbol.equals("{")) {
        String type = currentSymbol;
        String name = nextSymbol();
        nextSymbol(); // =
        String value = nextSymbol();
        nextSymbol(); // ;
        nextSymbol();
        if (type.equals("int")) {
          if (value.contains(".")) {
            System.out.println(
                "error message:line "
                    + reader.getLineNumber()
                    + ",realnum can not be translated into int type");
            error = true;
          } else {
            intMap.put(name, Integer.parseInt(value));
          }
        } else {
          floatMap.put(name, Float.parseFloat(value));
        }
      }

      nextSymbol(); // {

      while (currentSymbol != null && !currentSymbol.equals("}")) {
        if (currentSymbol.equals("if")) {
          nextSymbol(); // (
          String left = nextSymbol();
          String op = nextSymbol();
          String right = nextSymbol();
          nextSymbol(); // )
          nextSymbol(); // then
          nextSymbol();

          boolean result = true;
          if (!error) {
            float val0 = intMap.containsKey(left) ? intMap.get(left) : floatMap.get(left);
            float val1 = intMap.containsKey(right) ? intMap.get(right) : floatMap.get(right);
            result = switch (op) {
              case "==" -> val0 == val1;
              case "!=" -> val0 != val1;
              case "<" -> val0 < val1;
              case "<=" -> val0 <= val1;
              case ">" -> val0 > val1;
              default -> val0 >= val1;
            };
          }
          if (result) {
            calc();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
          } else {
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            nextSymbol();
            calc();
          }
        } else {
          if (currentSymbol.equals(";")) {
            nextSymbol();
          }
          calc();
        }
      }

      if (!error) {
        for (String key : new String[]{"a", "b", "c"}) {
          System.out.print(key + ": ");
          if (intMap.containsKey(key)) {
            System.out.println(intMap.get(key));
          } else {
            System.out.println(Math.round(floatMap.get(key) * 100) / 100.0f);
          }
        }
      }
    }

    private void calc() throws IOException {
      String left = currentSymbol;
      nextSymbol(); // =
      String right0 = nextSymbol();
      String op = nextSymbol();
      String right1 = nextSymbol();

      if (error) {
        if (op.equals("/") && right1.equals("0")) {
          System.out.println("error message:line " + reader.getLineNumber() + ",division by zero");
        }
        nextSymbol();
        nextSymbol();
        return;
      }
      float val0, val1;
      if (intMap.containsKey(right0)) {
        val0 = intMap.get(right0);
      } else if (floatMap.containsKey(right0)) {
        val0 = floatMap.get(right0);
      } else {
        val0 = Float.parseFloat(right0);
      }
      if (intMap.containsKey(right1)) {
        val1 = intMap.get(right1);
      } else if (floatMap.containsKey(right1)) {
        val1 = floatMap.get(right1);
      } else {
        val1 = Float.parseFloat(right1);
      }

      float result = switch (op) {
        case "+" -> val0 + val1;
        case "-" -> val0 - val1;
        case "*" -> val0 * val1;
        default -> val0 / val1;
      };

      if (intMap.containsKey(left)) {
        intMap.put(left, (int) result);
      } else {
        floatMap.put(left, result);
      }

      if (!nextSymbol().equals(";")) {
        nextSymbol();
        nextSymbol();
        if (intMap.containsKey(left)) {
          intMap.put(left, (int) result - 1);
        } else {
          floatMap.put(left, result - 1);
        }
      }
      nextSymbol();
    }
  }
}
