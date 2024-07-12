package parser.base;

import java.io.*;

import builders.*;
import lexer.*;

/* Compile:
 * 
 * javac -d bin -cp bin src/parser/base/Parser.java
*/

public abstract class Parser {

    // Lexer instance for tokenization
    protected Lexer L;
    // BufferedReader for reading characters
    protected BufferedReader buffer;
    // Current token being looked at
    protected Token look;

    // Constructor for Parser
    public Parser(Lexer lexer, BufferedReader buffer) {
        this.L = lexer;
        this.buffer = buffer;
        move();
    }

    protected abstract void start();

    // Method to advance to the next token
    private void move() {
        // Scan the input using the Lexer
        look = L.scan(buffer);
        System.out.println("Token: " + look);
    }

    // Method for handling parsing errors
    protected void error(String msg) {
        throw new Error("Syntax Error: near line " + Lexer.line + " " + msg);
    }

    // Method to match the current token with an expected token
    protected void match(int t) {
        if (look.tag == t) {
            // If not end-of-file token, move to the next token
            if (look.tag != Tag.EOF)
                move();
        } else
            error("Unexpected token. Expected token with tag " + t + " but found token with tag " + look.tag);
    }
}