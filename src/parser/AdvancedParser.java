package parser;

import java.io.*;

import builders.*;
import lexer.*;
import parser.base.Parser;

/* Compile and Run:
 * 
 * javac -d bin -cp bin src/parser/base/Parser.java
 * javac -d bin -cp bin src/parser/AdvancedParser.java; java -cp bin parser.AdvancedParser
*/

public class AdvancedParser extends Parser {

    // Constructor for Parser
    public AdvancedParser(Lexer lexer, BufferedReader buffer) {
        super(lexer, buffer);
    }

    public void start() {
        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case Tag.IF:
            case '{':
                statlist();
                match(Tag.EOF);
                break;
            default:
                error("Unexpected token in <start>: " + look.tag);
        }
    }

    private void statlist() {
        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case Tag.IF:
            case '{':
                stat();
                statlistp();
                break;
            default:
                error("Unexpected token in <statlist>: " + look.tag);
        }
    }

    private void statlistp() {
        switch (look.tag) {
            case ';':
                match(look.tag);
                stat();
                statlistp();
                break;
            case '}':
            case Tag.EOF:
                // Ɛ
                break;
            default:
                error("Unexpected token in <statlistp>: " + look.tag);
        }
    }

    private void stat() {
        switch (look.tag) {
            case Tag.ASSIGN:
                match(look.tag);
                assignlist();
                break;
            case Tag.PRINT:
                match(look.tag);
                match('(');
                exprlist();
                match(')');
                break;
            case Tag.READ:
                match(look.tag);
                match('(');
                idlist();
                match(')');
                break;
            case Tag.FOR:
                match(look.tag);
                match('(');
                statfor();
                match(')');
                match(Tag.DO);
                stat();
                break;
            case Tag.IF:
                match(look.tag);
                match('(');
                bexpr();
                match(')');
                stat();
                statelse();
                break;
            case '{':
                match(look.tag);
                statlist();
                match('}');
                break;
            default:
                error("Unexpected token in <stat>: " + look.tag);
        }
    }

    private void statfor() {
        switch (look.tag) {
            case Tag.ID:
                match(look.tag);
                match(Tag.INIT);
                expr();
                match(';');
                bexpr();
                break;
            case Tag.RELOP:
                bexpr();
                break;
            default:
                error("Unexpected token in <statfor>: " + look.tag);
        }
    }

    private void statelse() {
        switch (look.tag) {
            case Tag.ELSE:
                match(look.tag);
                stat();
                match(Tag.END);
                break;
            case Tag.END:
                match(look.tag);
                break;
            default:
                error("Unexpected token in <statelse>: " + look.tag);
        }
    }

    private void assignlist() {
        switch (look.tag) {
            case '[':
                match(look.tag);
                expr();
                match(Tag.TO);
                idlist();
                match(']');
                assignlistp();
                break;
            default:
                error("Unexpected token in <assignlist>: " + look.tag);
        }
    }

    private void assignlistp() {
        switch (look.tag) {
            case '[':
                match(look.tag);
                expr();
                match(Tag.TO);
                idlist();
                match(']');
                assignlistp();
                break;
            case '}':
            case ';':
            case Tag.ELSE:
            case Tag.END:
            case Tag.EOF:
                // Ɛ
                break;
            default:
                error("Unexpected token in <assignlistp>: " + look.tag);
        }
    }

    private void idlist() {
        switch (look.tag) {
            case Tag.ID:
                match(look.tag);
                idlistp();
                break;
            default:
                error("Unexpected token in <idlist>: " + look.tag);
        }
    }

    private void idlistp() {
        switch (look.tag) {
            case ',':
                match(look.tag);
                match(Tag.ID);
                idlistp();
                break;
            case ']':
            case ')':
                // Ɛ
                break;
            default:
                error("Unexpected token in <idlistp>: " + look.tag);
        }
    }

    private void bexpr() {
        switch (look.tag) {
            case Tag.RELOP:
                match(Tag.RELOP);
                expr();
                expr();
                break;
            default:
                error("Unexpected token in <bexpr>: " + look.tag);
        }
    }

    private void expr() {
        switch (look.tag) {
            case '+':
            case '*':
                match(look.tag);
                match('(');
                exprlist();
                match(')');
                break;
            case '-':
            case '/':
                match(look.tag);
                expr();
                expr();
                break;
            case Tag.NUM:
            case Tag.ID:
                match(look.tag);
                break;
            default:
                error("Unexpected token in <expr>: " + look.tag);
        }
    }

    private void exprlist() {
        switch (look.tag) {
            case '+':
            case '-':
            case '*':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();
                exprlistp();
                break;
            default:
                error("Unexpected token in <exprlist>: " + look.tag);
        }
    }

    private void exprlistp() {
        switch (look.tag) {
            case ',':
                match(look.tag);
                expr();
                exprlistp();
                break;
            case ')':
                // Ɛ
                break;
            default:
                error("Unexpected token in <exprlistp>: " + look.tag);
        }
    }

    public static void main(String[] args) {
        // create a new Lexer instance
        Lexer L = new Lexer();
        // specify the path of the file to analyze
        String path = "utils/test/test.txt";

        try {
            // create a BufferedReader to read characters from the file
            BufferedReader buffer = new BufferedReader(new FileReader(path));
            // create a new Parser instance
            AdvancedParser P = new AdvancedParser(L, buffer);

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