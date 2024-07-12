package lexer;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

import dfa.*;
import builders.*;

/* Compile and Run:
 * 
 * javac -d bin -cp bin src/builders/*.java
 * javac -d bin -cp bin src/lexer/Lexer.java; java -cp bin lexer.Lexer
*/

/* Programmer's Notes:
 * 
 * I decided to slightly rewrite the basic structure of the lexer in 
 * order to make the code more readable, for the same reason I decided 
 * to use my DFA class regarding identifier generation, instead of the 
 * standard proposed during the lab lessons.
*/

public class Lexer {

    // lines of the file
    public static int line = 1;
    // last character read from the buffer
    private static char peek = ' ';
    // map to get the token from the char
    private final Map<Character, Token> tokens = Map.ofEntries(
        // single characters
        Map.entry('!', Token.not),
        Map.entry('(', Token.lpt),
        Map.entry(')', Token.rpt),
        Map.entry('[', Token.lpq),
        Map.entry(']', Token.rpq),
        Map.entry('{', Token.lpg),
        Map.entry('}', Token.rpg),
        Map.entry('+', Token.plus),
        Map.entry('-', Token.minus),
        Map.entry('*', Token.mult),
        Map.entry('/', Token.div),
        Map.entry(';', Token.semicolon),
        Map.entry(',', Token.comma)
    );
    // map to get the keywords from string
    private final Map<String, Word> keywords = Map.ofEntries(
        // keywords
        Map.entry("assign", Word.assign),
        Map.entry("to",     Word.to),
        Map.entry("if",     Word.iftok),
        Map.entry("else",   Word.elsetok),
        Map.entry("do",     Word.dotok),
        Map.entry("for",    Word.fortok),
        Map.entry("begin",  Word.begin),
        Map.entry("end",    Word.end),
        Map.entry("print",  Word.print),
        Map.entry("read",   Word.read)
    );
    // map to get the double and compare characters from string
    private final Map<String, Word> words = Map.ofEntries(
        // double characters
        Map.entry(":=",     Word.init),
        Map.entry("==",     Word.eq),
        Map.entry("||",     Word.or),
        Map.entry("&&",     Word.and),
        // compare characters
        Map.entry("<",      Word.lt),
        Map.entry(">",      Word.gt),
        Map.entry("<=",     Word.le),
        Map.entry(">=",     Word.ge),
        Map.entry("<>",     Word.ne)
    );

    // method for reading the next character
    private void read(BufferedReader buffer) {
        try {
            // attempt to read the next char
            peek = (char) buffer.read();
        } catch (IOException exc) {
            // if an IOException occurs (e.g., end of file), set 'peek' to -1
            peek = (char) -1;
        }
    }

    // flag to indicate whether the next token should be treated as a division operator
    private static boolean division = false;
    // lexical analyzer
    public Token scan(BufferedReader buffer) {
        // sets the division flag before each loop
        division = false;

        // skip all the white space character ' ', '\r', '\t', '\n'
        skipWhitespace(buffer);

        // If the division flag is set, return the division operator token '/'
        if (division)
            return tokens.get('/');

        switch (peek) {
            case '!', '(', ')', '[', ']', '{', '}', '+', '-', '*', ';', ',':
                // handle all the single character
                return handleSingleCharacterToken();
            case '&', '|', '=', ':':
                // handle the double character (check for the init)
                return handleDoubleCharacterToken(buffer, peek == ':' ? '=' : peek);
            case '<':
                // check if its 'less-equals', 'not-equals' or just 'less'
                return handleCompareCharacterToken(buffer, '=', '>');
            case '>':
                // check if its 'greater-equals' or just 'greater'
                // nullifying value as second
                return handleCompareCharacterToken(buffer, '=' , '0');
            case (char) Tag.EOF:
                // check if EOF has been reached
                return Token.eof;
            default:    
                // check if char is a letter or underscore
                if(Character.isLetter(peek) || peek == '_' )
                    return handleIdentifiers(buffer);

                // check if char is a digit
                if(Character.isDigit(peek))
                    // identify the number and handle it
                    return handleNumbers(buffer);
                
                System.err.println("Erroneous character: " + peek);
                return null;
        }
    }

    // method to skip all whitespace ' ' '\r' '\t' '\n'
    private void skipWhitespace(BufferedReader buffer) {

        // check also if there are whitespaces or comments
        while (Character.isWhitespace(peek) || peek == '/') {
            // then manage it
            if (peek == '/')
                skipSingleLineComment(buffer);
            else {
                while (Character.isWhitespace(peek)) {
                    if (peek == '\n')
                        // increment the line counter
                        line++;
                    read(buffer);
                }
            }
        }
    }

    // method to skip all the single-line comment '//'
    private void skipSingleLineComment(BufferedReader buffer) {
        read(buffer);

        // check if the comment is 'single' or 'multi' -line
        if (peek == '/') {
            // loop while line end or file end
            while (peek != '\n' && peek != (char) -1)
                read(buffer);
        } else if (peek == '*')
            skipMultilineComment(buffer);
        else
            division = true;
    }

    // method to skip all the multi-line comment '/* */'
    private void skipMultilineComment(BufferedReader buffer) {
        read(buffer);
        // flag to check if the comment has been closed
        boolean flag = true;

        // loop to iterate through characters until the end of the multiline comment is
        // found
        while (flag) {
            // check if EOF has been reached
            if (peek == (char) -1) {
                System.out.println("Erroneous character on line: " + line + " (comment not closed)");
                // exit the loop, error detected
                flag = false;
            }

            if (peek == '*') {
                // read the next character
                read(buffer);

                if (peek == '/') {
                    read(buffer);
                    // exit the loop, multiline comment has been closed
                    flag = false;
                }
            }

            // read the next character and continue the loop
            read(buffer);
        }
    }

    // method to handle all the single character token '+' '-' '*' ';' ',' '(' '[' '{' '}' ']' ')'
    private Token handleSingleCharacterToken() {
        char current = peek;
        peek = ' ';
        // return the token using the key on the map
        return tokens.get(current);
    }

    // method to handle all the double character token '&&' '||' '==' ':='
    private Token handleDoubleCharacterToken(BufferedReader buffer, char expected) {
        // save the current character
        char current = peek;
        // read the next
        read(buffer);

        // check if the next matches the expected character 
        if (peek == expected) {
            // concatenate the chars to form the key
            String key = String.valueOf(current) + String.valueOf(peek);
            // consume the second character
            peek = ' ';
            // return the word using the key on the map
            return words.get(key);
        }
        
        System.err.println("Erroneous character after " + current + ": " + peek);
        return null;
    }

    // method to handle the compare character token '<' '>'
    private Token handleCompareCharacterToken(BufferedReader buffer, char first, char second) {
        // save the current character
        char current = peek;
        // read the next
        read(buffer);

        // check if the next matches the first expected character
        if(peek == first) {
            // concatenate chars to form the key
            String word = String.valueOf(current) + String.valueOf(peek);
            // consume the character
            peek = ' ';
            // return the word using the key on the map
            return words.get(word);
        }

        // check if the next matches the second expected character (only in case of '<')
        if(peek == second && current == '<') {
            // concatenate chars to form the key
            String word = String.valueOf(current) + String.valueOf(peek);
            // consume the character
            peek = ' ';
            // return the word using the key on the map
            return words.get(word);
        }
        
        // if the next does not matches the first or second expected character
        if(peek != second)
            // then return the default word using the key on the map
            return words.get(String.valueOf(current));
        
        System.err.println("Erroneous character after " + current + " : " + peek);
        return null;
    }

    // method to handle all the possibile keywords or identifiers
    private Word handleIdentifiers(BufferedReader buffer) {
        // initialize an empty string to store the number
        String key = "";

        // reading characters while they are letters or underscore 
        while(Character.isLetter(peek) || peek == '_') {
            key += peek;
            read(buffer);
        }

        switch(key) {
            // check if identifier is a keyword
            case "assign":
            case "to":
            case "if":
            case "else":
            case "do":
            case "for":
            case "begin":
            case "end":
            case "print":
            case "read":
                // handle all keywords
                return keywords.get(key);
            default:
                // handle identifier that are not keywords
                /* Note:
                 * At the end of the project I decided to use my 
                 * own DFA class instead of the 'standard template'
                */
                DFA M = javaDFA();
                // check if string is accepted
                if(M.scan(key))
                    return new Word(Tag.ID, key);
                
                System.out.println("Erroneous character: " + key);
                return null;
        }
    }

    // method to handle all the numbers
    private NumberTok handleNumbers(BufferedReader buffer) {
        // initialize an empty string to store the number
        String key = "";

        // reading characters while they are digits 
        while(Character.isDigit(peek)) {
            // concatenate all the digits
            key += peek;
            read(buffer);
        }

        // additional check for leading zero followed by additional digits
        if(key.charAt(0) == '0' && key.length() > 1) {
            System.out.println("Erroneous character: " + peek);
            return null;
        }

        // return a NumberTok obj
        return new NumberTok(Integer.valueOf(key));
    }

    // set the dfa
    private DFA javaDFA() {
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
        
        return new DFA(Q, A, delta, q0, F);
    }

    public static void main(String[] args) {
        // create a new Lexer instance
        Lexer L = new Lexer();
        // specify the path of the file to analyze
        String path = "utils/test/test.txt";

        try {
            // create a BufferedReader to read characters from the file
            BufferedReader buffer = new BufferedReader(new FileReader(path));
            // Start the lexical analysis by scanning the first token
            Token token = L.scan(buffer);

            // continue scanning and printing tokens until the end of the file is reached
            while (token != null && token.tag != Tag.EOF) {
                System.out.println("Scan: " + token);
                // get the next token
                token = L.scan(buffer);
            }
            // print the last token
            System.out.println("Scan: " + token);

            // close the BufferedReader to release resources
            buffer.close();
        } catch (IOException E) {
            // handle IOException by printing the stack trace
            E.printStackTrace();
        }
    }
}