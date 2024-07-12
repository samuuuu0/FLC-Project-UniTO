package valutatore;

import java.io.*;

import builders.*;
import lexer.*;
import parser.base.Parser;

/* Compile and Run:
 * 
 * javac -d bin -cp bin src/parser/base/Parser.java
 * javac -d bin -cp bin src/valutatore/Valutatore.java; java -cp bin valutatore.Valutatore
*/

public class Valutatore extends Parser {

    // Constructor for Valutatore
    public Valutatore(Lexer lexer, BufferedReader buffer) {
        super(lexer, buffer);
    }

    public void start() {
        int expr_val = 0;

        switch (look.tag) {
            case '(':
            case Tag.NUM:
                expr_val = expr();
                match(Tag.EOF);
                break;
            default:
                error("Unexpected token in <start>: " + look.tag);
        }

        System.out.println(expr_val);
    }

    private int expr() { 
	    int term_val = 0, expr_val = 0;

	    switch (look.tag) {
            case '(':
            case Tag.NUM:
                term_val = term();
                expr_val = exprp(term_val);
                break;
            default:
                error("Unexpected token in <expr>: " + look.tag);
        }

        return expr_val;
    }

    private int exprp(int exprp_i) {
	    int term_val = 0, exprp_val = 0;

        switch (look.tag) {
            case '+':
                match('+');
                term_val = term();
                exprp_val = exprp(exprp_i + term_val);
                break;
            case '-':
                match('-');
                term_val = term();
                exprp_val = exprp(exprp_i - term_val);
                break;
            case ')':
            case Tag.EOF:
                exprp_val = exprp_i;
                break;
            default:
                error("Unexpected token in <exprp>: " + look.tag);
        }

        return exprp_val;
    }

    private int term() {
        int fact_val = 0, term_val = 0;

        switch(look.tag) {
            case '(':
            case Tag.NUM:
                fact_val = fact();
                term_val = termp(fact_val);
                break;
            case Tag.EOF:
                break;
            default:
                error("Unexpected token in <term>: " + look.tag);
        }

        return term_val;
    }
    
    private int termp(int termp_i) {
        int fact_val = 0, termp_val = 0; 

        switch (look.tag) {
            case '*':
                match('*');
                fact_val = fact();
                termp_val = termp(termp_i * fact_val);
                break;
            case '/':
                match('/');
                fact_val = fact();
                termp_val = termp(termp_i / fact_val);
                break;
            case ')':
            case '+':
            case '-':                
            case Tag.EOF:
                termp_val = termp_i;
                break;
            default:
                error("Unexpected token in <termp>: " + look.tag);
        }

        return termp_val;
    }
    
    private int fact() {
        int fact_val = 0;

        switch (look.tag) {
            case '(':
                match('(');
                fact_val = expr();
                match(')');
                break;
            case Tag.NUM:
                NumberTok num = (NumberTok) look;
                fact_val = num.lexeme;
                match(Tag.NUM);
                break;
            default:
                error("Unexpected token in <fact>: " + look.tag);
        }

        return fact_val;
    }

    public static void main(String[] args) {
        // create a new Lexer instance
        Lexer L = new Lexer();
        // specify the path of the file to analyze
        String path = "utils/test/test.txt";

        try {
            // create a BufferedReader to read characters from the file
            BufferedReader buffer = new BufferedReader(new FileReader(path));
            // create a new Valutatore instance
            Valutatore V = new Valutatore(L, buffer);

            // start scanning
            V.start();

            // close the BufferedReader to release resources
            buffer.close();
        } catch (IOException E) {
            // handle IOException by printing the stack trace
            E.printStackTrace();
        }
    }
}
