package Calculator;

/*
 * A program that calculates mathematical equations.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class Calculator {

    static char[] operationsList = new char[]{'^', '*', '/', '+', '-', '(', ')'};
    static String[] operationsList1 = new String[]{"sin", "cos", "tan", "atan", "sqrt", "log10", "log2"};
    static int counter = 0;

    public static void main(String[] args) throws Exception {

        try {
        String formula = args[0];
        for (String str : args) {
            System.out.println(str);
        }
        HashMap<String, Double> variables = writeVariables(args);
            System.out.println(formula + " = " + calculate(formula, variables));
        } catch (Exception e) {
            System.out.println("Not a correct mathematical equations");
        }
    }

    //Read variables and their values(if they are)
    private static HashMap<String, Double> writeVariables(String[] args) {
        if (args.length > 1) {
            HashMap<String, Double> variables = new HashMap<>();
            for (int i = 1; i < args.length; i++) {
                String variableName = String.valueOf(args[i].charAt(0));
                String variableValue0 = "";

                for (int j = 0; j < args[i].length(); j++) {
                    if (args[i].charAt(j) == '=') {
                        for (int k = j + 1; k < args[i].length(); k++) {
                            if ((int) args[i].charAt(k) > 47 && (int) args[i].charAt(k) < 58 ||
                                    args[i].charAt(k) == '.' ||
                                    args[i].charAt(k) == '-') {
                                variableValue0 += args[i].charAt(k);
                            }
                        }
                    }
                }

                if (!variableValue0.equals("")) {
                    Double variableValue = Double.valueOf(variableValue0);
                    variables.put(variableName, variableValue);
                }
            }
            return variables;
        }
        return null;
    }

    /**
     * Function who calculate some formula
     *
     * @param formula   the formula you need calculate
     * @param variables HashMap with variables and their values
     * @return Solution of the equation
     */
    static double calculate(String formula, HashMap<String, Double> variables) throws Exception {
        StringBuilder formula1 = new StringBuilder(formula);
        //replace variables and their values in formula
        if (variables != null) {
            for (String key : variables.keySet()) {
                for (int i = 0; i < formula1.length(); i++) {
                    if (key.equals(String.valueOf(formula1.charAt(i)))) {
                        if (formula.charAt(i - 1) > 95 && formula.charAt(i - 1) < 123 ||
                                formula.charAt(i + 1) > 95 && formula.charAt(i + 1) < 123) {
                            continue;
                        }
                        formula1.deleteCharAt(i);
                        formula1.insert(i, variables.get(key));
                    }
                }
            }
        }

        ArrayList<Double> values = new ArrayList<>();
        ArrayList<String> operations = new ArrayList<>();
        parseFormula(formula1.toString(), values, operations);
        ArrayList<Integer> precedence = new ArrayList<>();
        checkPrecedence(precedence, operations);
        int maxPrecedence = maxPrecedence(precedence);
        //Starting from max precedence calculate the equation
        for (int i = maxPrecedence; i > 0; i--) {
            performCalculations(i, operations, values, precedence);
        }

        return values.get(0);
    }

    /**
     * @param precedence Array with precedence of operations.
     * @return Maximum precedence
     */
    private static int maxPrecedence(ArrayList<Integer> precedence) {
        int max = 0;
        for (int pr : precedence) {
            if (pr >= max) {
                max = pr;
            }
        }
        return max;
    }

    /**
     * Method checks precedence of each operation
     *
     * @param precedence Array in which we will write precedence of operations.
     * @param operations Array with operations
     */
    private static void checkPrecedence(ArrayList<Integer> precedence, ArrayList<String> operations) throws Exception {
        int growth = 0;
        for (int i = 0; i < operations.size(); i++) {
            if (operations.get(i).equals("(")) {
                growth += 4;
                operations.remove(i);
                i--;
                continue;
            }
            if (operations.get(i).equals(")")) {
                growth -= 4;
                operations.remove(i);
                i--;
                continue;
            }
            precedence.add(precedence(operations.get(i)) + growth);
        }
    }


    // Do some calculations according to the precedence
    private static void performCalculations(int i, ArrayList<String> operations, ArrayList<Double> values, ArrayList<Integer> precedence) {
        for (int j = 0; j < operations.size(); j++) {
            if (precedence.get(j) == i) {
                boolean cont = false;
                //if operation from operationsList1
                for (String op : operationsList1) {
                    if (op.equals(operations.get(j))) {
                        counter = 0;
                        for (String op1 : operationsList1) {
                            for (int k = 0; k < j; k++) {
                                if (op1.equals(operations.get(k)))
                                    counter++;
                            }
                        }
                        values.set(j - counter, applyOp(values.get(j - counter), operations.get(j)));
                        operations.remove(j);
                        precedence.remove(j);
                        j--;
                        cont = true;
                        break;
                    }
                }
                if (cont) continue;
                //if operation from operationsList
                for (String op1 : operationsList1) {
                    for (int k = 0; k < j; k++) {
                        if (op1.equals(operations.get(k)))
                            counter++;
                    }
                }
                values.set(j - counter, applyOp(values.get(j - counter), values.get(j + 1 - counter), operations.get(j)));
                values.remove(j + 1 - counter);
                operations.remove(j);
                precedence.remove(j);
                j--;
                counter = 0;
            }
        }
    }

    /**
     * parsing the formula, divide the formula into two arrays
     *
     * @param formula    the formula you need parse
     * @param values     ArrayList in which we will write numerical values
     * @param operations ArrayList in which we will write math operations
     */
    private static void parseFormula(String formula, ArrayList<Double> values, ArrayList<String> operations) {
        //add operations to appropriate ArrayList (except minus)
        for (int i = 0; i < formula.length(); i++) {
            for (char op : operationsList) {
                if (formula.charAt(i) == op && formula.charAt(i) != '-')
                    operations.add(String.valueOf(formula.charAt(i)));
            }
            String op = "";
            if (formula.charAt(i) > 95 && formula.charAt(i) < 123) {
                do {
                    op += formula.charAt(i);
                    i++;
                } while (formula.charAt(i) > 95 && formula.charAt(i) < 123 || (int) formula.charAt(i) > 47 && (int) formula.charAt(i) < 58);
                i--;
            }
            if (!op.equals("")) {
                operations.add(op);
                continue;
            }
            String variableValue = "";
            //add numerical values to appropriate ArrayList
            if ((int) formula.charAt(i) > 47 && (int) formula.charAt(i) < 58 ||
                    formula.charAt(i) == '-' && isUnary(formula, i, operationsList)) {

                variableValue += formula.charAt(i);
                for (int k = i + 1; k < formula.length(); k++) {

                    i++;
                    if ((int) formula.charAt(k) > 47 && (int) formula.charAt(k) < 58 ||
                            formula.charAt(k) == '.') {
                        variableValue += formula.charAt(k);
                        continue;
                    }
                    i--;
                    break;

                }

                //add minus to appropriate ArrayList
            } else if (formula.charAt(i) == '-' && !isUnary(formula, i, operationsList)) {
                operations.add("-");
            }

            if (!variableValue.equals("")) values.add(Double.parseDouble(variableValue));

        }
    }

    /**
     * Checks minus for unary.
     *
     * @param formula        formula
     * @param i              number of minus in formula
     * @param operationsList all math operations
     * @return true if unary or false if not
     */
    private static boolean isUnary(String formula, int i, char[] operationsList) {
        if (i != 0) {
            for (int j = i - 1; j >= 0; j--) {
                for (char op : operationsList) {
                    if (formula.charAt(j) == op && formula.charAt(j) != ')') {
                        for (int k = i + 1; k < 3 + i; k++) {
                            if ((int) formula.charAt(k) > 47 && (int) formula.charAt(k) < 58) {
                                return true;
                            }
                        }
                    }
                }
                if ((int) formula.charAt(j) > 47 && (int) formula.charAt(j) < 58) {
                    return false;
                }
            }
        }
        return false;
    }


    /**
     * find precedence of math operator
     *
     * @param op math operator
     * @return precedence of math operator
     */
    static int precedence(String op) throws Exception {
        if (op.equals("+") || op.equals("-"))
            return 1;
        if (op.equals("*") || op.equals("/"))
            return 2;
        if (op.equals("^"))
            return 3;
        if (op.equals("sin") || op.equals("cos") || op.equals("tan") || op.equals("atan") || op.equals("sqrt") || op.equals("log10") || op.equals("log2"))
            return 4;
        throw new Exception();
    }

    // Function to perform arithmetic operations
    static double applyOp(double a, double b, String op) {
        return switch (op) {
            case "+" -> a + b;
            case "-" -> a - b;
            case "*" -> a * b;
            case "/" -> a / b;
            case "^" -> Math.pow(a, b);
            default -> 0;
        };
    }

    // Function to perform arithmetic operations
    static double applyOp(double a, String op) {
        return switch (op) {
            case "cos" -> Math.cos(a);
            case "sin" -> Math.sin(a);
            case "tan" -> Math.tan(a);
            case "atan" -> Math.atan(a);
            case "sqrt" -> Math.sqrt(a);
            case "log10" -> Math.log(a) * 0.43429;
            case "log2" -> Math.log(a) / Math.log(2);
            default -> 0;
        };
    }
}
