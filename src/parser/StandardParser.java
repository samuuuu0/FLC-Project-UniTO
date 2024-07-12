package parser;

import java.io.*;

import builders.*;
import lexer.*;
import parser.base.Parser;

/* Compile and Run:
 * 
 * javac -d bin -cp bin src/parser/base/Parser.java
 * javac -d bin -cp bin src/parser/StandardParser.java; java -cp bin parser.StandardParser
*/

public class StandardParser extends Parser {

    // Constructor for Parser
    public StandardParser(Lexer lexer, BufferedReader buffer) {
        super(lexer, buffer);
    }

    public void start() {
        switch (look.tag) {
            case '(':
            case Tag.NUM:
                expr();
                match(Tag.EOF);
                break;
            default:
                error("Unexpected token in <start>: " + look.tag);
        }
    }

    private void expr() {
        switch (look.tag) {
            case '(':
            case Tag.NUM:
                term();
                exprp();
                break;
            default:
                error("Unexpected token in <expr>: " + look.tag);
        }
    }

    private void exprp() {
        switch (look.tag) {
            case '+':
            case '-':
                match(look.tag);
                term();
                exprp();
                break;
            case ')':
            case Tag.EOF:
                break;
            default:
                error("Unexpected token in <exprp>: " + look.tag);
        }
    }

    private void term() {
        switch (look.tag) {
            case '(':
            case Tag.NUM:
                fact();
                termp();
                break;
            case Tag.EOF:
                break;
            default:
                error("Unexpected token in <term>: " + look.tag);
        }
    }

    private void termp() {
        switch (look.tag) {
            case '*':
            case '/':
                match(look.tag);
                fact();
                termp();
                break;
            case ')':
            case '+':
            case '-':                
            case Tag.EOF:
                break;
            default:
                error("Unexpected token in <termp>: " + look.tag);
        }
    }

    private void fact() {
        switch (look.tag) {
            case '(':
                match('(');
                expr();
                match(')');
                break;
            case Tag.NUM:
                match(Tag.NUM);
                break;
            default:
                error("Unexpected token in <fact>: " + look.tag);
        }
    }

    // Main method for testing the Parser
    public static void main(String[] args) {
        // create a new Lexer instance
        Lexer L = new Lexer();
        // specify the path of the file to analyze
        String path = "utils/test/test.txt";

        try {
            // create a BufferedReader to read characters from the file
            BufferedReader buffer = new BufferedReader(new FileReader(path));
            // create a new Parser instance
            StandardParser P = new StandardParser(L, buffer);

            // start scanning
            P.start();

            System.out.println("Input OK");

            // close the BufferedReader to release resources
            buffer.close();
        } catch (IOException E) {
            // handle IOException by printing the stack trace
            E.printStackTrace();
        }
    }
}