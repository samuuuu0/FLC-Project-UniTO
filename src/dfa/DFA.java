package dfa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;

/* Compile and Run:
 * 
 * javac -d bin -cp bin src/dfa/DFA.java; java -cp bin dfa.DFA
*/

/* Why? 
 * I thought it would be better to create a skeleton on which any type of 
 * DFA could be implemented, in order to make it more practical and useful.
*/

/* How?
 * I tried to follow the DFA theory in order to create the code directly 
 * following the 5-tuple from which it's made up.
*/

/* Programmer's Notes:
 * 
 * this is not the final code or anything like that. Parts of the code could
 * be made even more flexible, for example, Genetics or Java Streams to make 
 * the code more readable.
 * Our course does not require this, so I have no plans to improve the code 
 * at this time (Maybe in the future for personal interests).
*/

// a deterministic finite automaton (dfa) M is a 5-tuple, (Q, Σ, δ, q0, F) consisting of:
public class DFA {

    // finite set of states
    /* Note:
     * used only during constructor checks, 
     * therefore we are not obliged to declare it in the class
     */
    // private Set<Integer> Q;
    // finite set of input symbols, called the alphabet
    private List<Predicate<Character>> A;
    // transition function (δ: Q × Σ → Q)
    private Map<Map.Entry<Integer, Predicate<Character>>, Integer> delta;
    // initial state
    private Integer q0;
    // set of accepted states
    private Set<Integer> F;

    // constructor
    public DFA(Set<Integer> Q, List<Predicate<Character>> A, Map<Map.Entry<Integer, Predicate<Character>>, Integer> delta, Integer q0, Set<Integer> F) {
        validate(Q, A, delta, q0, F);

        // all checks passed
        // this.Q      = new HashSet<>(Q);
        this.A      = new ArrayList<>(A);
        this.delta  = new HashMap<>(delta);
        this.q0     = q0;
        this.F      = new HashSet<>(F);
    }

    // validate arguments method
    private void validate(Set<Integer> Q, List<Predicate<Character>> A, Map<Map.Entry<Integer, Predicate<Character>>, Integer> delta, Integer q0, Set<Integer> F) {
        try {
            // check for valid argument (Q ≠ ∅ ...)
            if(Q == null || Q.isEmpty() || A == null || A.isEmpty() || F == null || F.isEmpty())
                throw new IllegalArgumentException("The set of states, accepted states and the alphabet cannot be null or empty");
            // q0 ∈ Q
            if(!Q.contains(q0))
                throw new IllegalArgumentException("The initial state must belong to the set of states");
            // F ⊆ Q
            if(!Q.containsAll(F))
                throw new IllegalArgumentException("The set of accepted states must be a subset of the set of states");

            // check delta arguments
            Set<Integer> domain = new HashSet<Integer>();
            Set<Integer> codomain = new HashSet<Integer>(delta.values());

            for(Entry<Integer, Predicate<Character>> element : delta.keySet())
                domain.add(element.getKey());

            for(int element : domain)
                if(!Q.contains(element))
                    throw new IllegalArgumentException("Error in the domain of the transition function");

            for(int element : codomain)
                if(!Q.contains(element))
                    throw new IllegalArgumentException("Error in the codomain of the transition function");
                    
        } catch (Exception e) {
            throw new IllegalArgumentException("Error during DFA creation: " + e.getMessage());
        }
    }

    // string scan method
    public boolean scan(String input) {

        /* for(char element : input.toCharArray()) {

            boolean validate = false;
            for(int i = 0; i < A.size() && !validate; i++)
                if(A.get(i).test(element)) 
                    validate = true;

            if(!validate)
                return false;
        } */

        // check if the input string is in the alphabet
        for (char element : input.toCharArray()) {
            // check if the current 'element' matches any predicate in the alphabet
            boolean validate = A.stream().anyMatch(predicate -> predicate.test(element));
        
            if (!validate)
                return false;
                // throw new IllegalArgumentException("The character '" + element + "' does not belong to the alphabet");
        }

        // check if the input string is accepted by the DFA
        int s = this.q0;
        // iterate the input string
        for(int i = 0; i < input.length(); i++) {

            boolean transition = false;
            // iterate to check the predicate
            for(Entry<Integer, Predicate<Character>> element : delta.keySet()) {
                if(element.getKey().equals(s) && element.getValue().test(input.charAt(i))) {
                    // assignament to next state
                    s = delta.get(element);
                    transition = true;
                    break;
                }
            }

            if(!transition)
                throw new IllegalArgumentException("No transition found for the current state '" + s + "' and the character '" + input.charAt(i) + "'");
        }

        return F.contains(s);
    }

    // java-like identifier
    private static boolean Exercise1_2() {
        // strings accepted by dfa
        String[] accepted = {"x", "flag1", "x2y2", "x_1", "lft_lab", "_temp", "x_1_y_2", "x__", "__5"};
        // strings rejected by dfa
        String[] rejected = {"5", "221B", "123", "9_to_5", "_"};

        // create the basis of DFA:
        // setting the set of the states
        Set<Integer> Q = new HashSet<>(Arrays.asList(0, 1, 2, 3));
        // setting the alphabet rules
        List<Predicate<Character>> A = Arrays.asList(
            c -> Character.isLetter(c),
            c -> Character.isDigit(c),
            c -> c == '_'
        );
        // setting the transition function
        Map<Map.Entry<Integer, Predicate<Character>>, Integer> delta = Map.of(
            // initial state
            Map.entry(0, c -> Character.isLetter(c)), 2,
            Map.entry(0, c -> Character.isDigit(c)), 3,
            Map.entry(0, c -> c == '_'), 1,
            // first state
            Map.entry(1, c -> Character.isLetter(c)), 2,
            Map.entry(1, c -> Character.isDigit(c)), 2,
            Map.entry(1, c -> c == '_'), 1,
            // second (accepted) state
            Map.entry(2, c -> true), 2,
            // third (dead) state
            Map.entry(3, c -> true), 3
        );
        // setting the set of the final states 
        Set<Integer> F = new HashSet<>(Arrays.asList(2));
        // setting the initial state
        int q0 = 0;
        // create the dfa
        DFA M = new DFA(Q, A, delta, q0, F);

        // check the accepted strings
        boolean x = true;
        for (String element : accepted)
            x = x && M.scan(element);

        // check the rejected strings
        boolean y = true;
        for (String element : rejected)
            y = y && !M.scan(element);

        // compare the results
        return x && y;
    }

    public static void main(String[] args) {
        System.out.println(Exercise1_2());
    }
}