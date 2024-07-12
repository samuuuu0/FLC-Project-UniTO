package dfa;

import java.util.Arrays;

/* Compile and Run:
 * 
 * javac -d bin -cp bin src/dfa/StandardDFA.java; java -cp bin dfa.StandardDFA
*/

/* Programmer's Notes:
 *
 * I have written all DFAs following the standard imposed by the code provided in 
 * the laboratory lessons, rather than using my DFA class, in case this helps with 
 * the evaluation.
*/

public class StandardDFA {

    public static boolean Exercise1_0(String s) {
        // current state of DFA
        int state = 0;
        // index for iterating the input string
        int i = 0;

        // iterating the string
        while (state >= 0 && i < s.length()) {
            // get the current character
            final char ch = s.charAt(i++);
            // state transitions
            switch (state) {
                // q0
                case 0:
                    if (ch == '0')
                        state = 1;
                    else if (ch == '1')
                        state = 0;
                    else
                        state = -1;
                    break;
                // q1
                case 1:
                    if (ch == '0')
                        state = 2;
                    else if (ch == '1')
                        state = 0;
                    else
                        state = -1;
                    break;
                // q2
                case 2:
                    if (ch == '0')
                        state = 3;
                    else if (ch == '1')
                        state = 0;
                    else
                        state = -1;
                    break;
                // q3
                case 3:
                    if (ch == '0' || ch == '1')
                        state = 3;
                    else
                        state = -1;
                    break;
            }
        }
        /*
         * Note: return true if the string is in the final state, else the string is not
         * accepted, than return false
         */
        return state == 3;
    }

    public static boolean Exercise1_1(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (ch == '0')
                        state = 1;
                    else if (ch == '1')
                        state = 1;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch == '0')
                        state = 1;
                    else if (ch == '1')
                        state = 0;
                    else
                        state = -1;
                    break;

                case 2:
                    if (ch == '0')
                        state = 2;
                    else if (ch == '1')
                        state = 0;
                    else
                        state = -1;
                    break;

                case 3:
                    if (ch == '0')
                        state = 3;
                    else if (ch == '1')
                        state = 0;
                    else
                        state = -1;
                    break;

                case 4:
                    if (ch == '0' || ch == '1')
                        state = 3;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 1 || state == 2 || state == 3;
    }

    public static boolean Exercise1_2(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (Character.isLetter(ch))
                        state = 2;
                    else if (Character.isDigit(ch))
                        state = 3;
                    else if (ch == '_')
                        state = 1;
                    else
                        state = -1;
                    break;

                case 1:
                    if (Character.isLetter(ch) || Character.isDigit(ch))
                        state = 2;
                    else if (ch == '_')
                        state = 1;
                    else
                        state = -1;
                    break;

                case 2:
                    if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')
                        state = 2;
                    else
                        state = -1;
                    break;

                case 3:
                    if (Character.isLetter(ch) || Character.isDigit(ch) || ch == '_')
                        state = 3;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 2;
    }

    public static boolean Exercise1_3(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (Character.isLetter(ch))
                        state = 4;
                    else if (Character.isDigit(ch) && (int) ch % 2 == 0)
                        state = 1;
                    else if (Character.isDigit(ch) && (int) ch % 2 != 0)
                        state = 2;
                    else
                        state = -1;
                    break;

                case 1:
                    if ((ch >= 'A' && ch <= 'K') || (ch >= 'a' && ch <= 'k'))
                        state = 3;
                    else if (Character.isDigit(ch) && (int) ch % 2 == 0)
                        state = 1;
                    else if (Character.isDigit(ch) && (int) ch % 2 != 0)
                        state = 2;
                    else if ((ch >= 'L' && ch <= 'Z') || (ch >= 'l' && ch <= 'z'))
                        state = 4;
                    else
                        state = -1;
                    break;

                case 2:
                    if ((ch >= 'L' && ch <= 'Z') || (ch >= 'l' && ch <= 'z'))
                        state = 3;
                    else if (Character.isDigit(ch) && (int) ch % 2 == 0)
                        state = 1;
                    else if (Character.isDigit(ch) && (int) ch % 2 != 0)
                        state = 2;
                    else if ((ch >= 'A' && ch <= 'K') || (ch >= 'a' && ch <= 'k'))
                        state = 4;
                    else
                        state = -1;
                    break;

                case 3:
                    if (Character.isLetter(ch))
                        state = 3;
                    else if (Character.isDigit(ch))
                        state = 4;
                    else
                        state = -1;
                    break;

                case 4:
                    if (Character.isLetter(ch) || Character.isDigit(ch))
                        state = 4;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 3;
    }

    public static boolean Exercise1_4(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (ch == '+' || ch == '-')
                        state = 1;
                    else if (Character.isDigit(ch))
                        state = 2;
                    else if (ch == '.')
                        state = 3;
                    else if (ch == 'e')
                        state = 10;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch == '.')
                        state = 3;
                    else if (Character.isDigit(ch))
                        state = 2;
                    else if (ch == '+' || ch == '-' || ch == 'e')
                        state = 10;
                    else
                        state = 1;
                    break;

                case 2:
                    if (ch == '.')
                        state = 3;
                    else if (Character.isDigit(ch))
                        state = 2;
                    else if (ch == 'e')
                        state = 8;
                    else if (ch == '+' || ch == '-')
                        state = 10;
                    break;

                case 3:
                    if (Character.isDigit(ch))
                        state = 4;
                    else if (ch == '+' || ch == '-' || ch == '.' || ch == 'e')
                        state = 10;
                    else
                        state = -1;
                    break;

                case 4:
                    if (Character.isDigit(ch))
                        state = 4;
                    else if (ch == 'e')
                        state = 6;
                    else if (ch == '+' || ch == '-' || ch == '.')
                        state = 10;
                    else
                        state = -1;
                    break;

                case 6:
                    if (ch == '+' || ch == '-' || Character.isDigit(ch))
                        state = 7;
                    else if (ch == '.' || ch == 'e')
                        state = 10;
                    else
                        state = -1;
                    break;

                case 7:
                    if (Character.isDigit(ch))
                        state = 6;
                    else if (ch == '+' || ch == '-' || ch == '.' || ch == 'e')
                        state = 10;
                    break;

                case 8:
                    if (ch == '+' || ch == '-' || Character.isDigit(ch))
                        state = 9;
                    else if (ch == '.' || ch == 'e')
                        state = 10;
                    else
                        state = -1;
                    break;

                case 9:
                    if (Character.isDigit(ch))
                        state = 9;
                    else if (ch == '.')
                        state = 3;
                    else if (ch == '+' || ch == '-' || ch == 'e')
                        state = 10;
                    break;

                case 10:
                    if (ch == '+' || ch == '-' || ch == '.' || ch == 'e' || Character.isDigit(ch))
                        state = 10;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 2 || state == 4 || state == 7 || state == 9;
    }

    public static boolean Exercise1_5(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (ch == '/')
                        state = 1;
                    else if (ch == '*' || ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch == '*')
                        state = 2;
                    else if (ch == '/' || ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 2:
                    if (ch == '/' || ch == 'a')
                        state = 2;
                    else if (ch == '*')
                        state = 3;
                    else
                        state = -1;
                    break;

                case 3:
                    if (ch == '*')
                        state = 3;
                    else if (ch == 'a')
                        state = 2;
                    else if (ch == '/')
                        state = 4;
                    else
                        state = -1;
                    break;

                case 4:
                    if (ch == '/' || ch == '*' || ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 5:
                    if (ch == '/' || ch == '*' || ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 4;
    }

    public static boolean Exercise1_6(String s) {
        int state = 0;
        int i = 0;

        while (state >= 0 && i < s.length()) {
            final char ch = s.charAt(i++);

            switch (state) {
                case 0:
                    if (ch == '/')
                        state = 1;
                    else if (ch == '*' || ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 1:
                    if (ch == '*')
                        state = 2;
                    else if (ch == '/' || ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 2:
                    if (ch == '*')
                        state = 3;
                    else if (ch == '/' || ch == 'a')
                        state = 2;
                    else
                        state = -1;
                    break;

                case 3:
                    if (ch == '/')
                        state = 4;
                    else if (ch == '*')
                        state = 3;
                    else if (ch == 'a')
                        state = 2;
                    else
                        state = -1;
                    break;

                case 4:
                    if (ch == '/')
                        state = 1;
                    else if (ch == 'a' || ch == '*')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 5:
                    if (ch == '/')
                        state = 1;
                    else if (ch == '*' || ch == 'a')
                        state = 5;
                    else
                        state = -1;
                    break;

                case 6:
                    if (ch == '/' || ch == '*' || ch == 'a')
                        state = 6;
                    else
                        state = -1;
                    break;
            }
        }

        return state == 4 || state == 5;
    }

    public static boolean check(String[] accepted, String[] rejected, DFAChecker checker) {
        // simplification of for-each using Java Streams

        // convert array into Stream and check if all elements of 'accepted' are true  
        boolean x = Arrays.stream(accepted).allMatch(s -> checker.Exercise(s));
        // convert array into Stream and check if all elements of 'rejected' are false 
        boolean y = Arrays.stream(rejected).allMatch(s -> !checker.Exercise(s));

        return x && y;
    }

    @FunctionalInterface
    interface DFAChecker {
        // abstract method
        boolean Exercise(String s);
    }

    public static void main(String[] args) {

        // accepted strings: contains three consecutive zeros
        // rejected strings: otherwise ...
        // System.out.println(Exercise1_0(args[0]) ? "YES" : "NOPE");

        // accepted strings: do not contains three consecutive zeros
        // rejected strings: otherwise ...
        // System.out.println(Exercise1_1(args[0]) ? "YES" : "NOPE");

        String accepted[] = { "x", "flag1", "x2y2", "x_1", "lft_lab", "_temp", "x_1_y_2", "x__", "__5" };
        String rejected[] = { "5", "221B", "123", "9_to_5", "_" };

        // String accepted[] = {"123456Bianchi", "654321Rossi", "2Bianchi", "122B"};
        // String rejected[] = {"123456Rossi", "654321Bianchi", "654322", "Rossi"};

        // String accepted[] = {"123", "123.5", ".567", "+7.5", "-.7", "67e10", "1e-2",
        // "-.7e2", "1e2.3"};
        // String rejected[] = {".", "e3", "123.", "+e6", "1.2.3", "4e5e6", "++3"};

        // String accepted[] = {"/****/", "/*a*a*/", "/*a/**/", "/**a///a/a**/", "/**/",
        // "/*/*/"};
        // String rejected[] = {"/*/", "/**/***/"};

        // String accepted[] = {"aaa/****/aa", "aa/*a*a*/", "aaaa", "/****/", "/*aa*/",
        // "*/a", "a/**/***a", "a/**/***/a", "a/**/aa/***/a"};
        // String rejected[] = {"aaa/*/aa", "a/**//***a", "aa/*aa"};

        System.out.println(check(accepted, rejected, StandardDFA::Exercise1_2));
    }
}