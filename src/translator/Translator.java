package translator;

import java.io.*;

import builders.*;
import lexer.*;
import parser.base.Parser;
import translator.builders.*;

/* Compile and Run:
 * 
 * javac -d bin -cp bin src/parser/base/Parser.java
 * javac -d bin -cp bin src/translator/builders/*.java
 * javac -d bin -cp bin src/translator/Translator.java; java -cp bin translator.Translator; java -jar utils/jasmin.jar -d bin utils/Output.j; java -cp bin Output
*/

public class Translator extends Parser {

    SymbolTable st = new SymbolTable();
    CodeGenerator code = new CodeGenerator();

    int count = 0;

    // Constructor for Translator
    public Translator(Lexer lexer, BufferedReader buffer) {
        super(lexer, buffer);
    }

    public void start() {
        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case Tag.IF:
            case '{': {
                int nextLabel = code.newLabel();

                statlist();
                match(Tag.EOF);

                code.emit(OpCode.GOto, nextLabel);
                code.emitLabel(nextLabel);
                break;
            }
            default:
                error("Unexpected token in <start>: " + look.tag);
        }
        
        try {
            code.toJasmin();
        } catch (java.io.IOException e) {
            System.out.println("I/O ERROR\n");
        }
    }

    private void statlist() {
        switch (look.tag) {
            case Tag.ASSIGN:
            case Tag.PRINT:
            case Tag.READ:
            case Tag.FOR:
            case Tag.IF:
            case '{': {
                int nextLabel = code.newLabel();
                stat();
                code.emit(OpCode.GOto, nextLabel);
                code.emitLabel(nextLabel);
                statlistp();
                break;
            }
            default:
                error("Unexpected token in <statlist>: " + look.tag);
        }
    }

    private void statlistp() {
        switch (look.tag) {
            case ';': {
                match(look.tag);

                int nextLabel = code.newLabel();
                stat();
                code.emit(OpCode.GOto, nextLabel);
                code.emitLabel(nextLabel);
                statlistp();
                break;
            }
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
                exprlist(true, '0');
                match(')');
                break;
            case Tag.READ:
                match(look.tag);
                match('(');
                idlist(true);
                match(')');
                break;
            case Tag.FOR: {
                int startLabel = code.newLabel();
                int endLabel = code.newLabel();

                match(look.tag);
                match('(');
                statfor(startLabel, endLabel);
                match(')');
                match(Tag.DO);
                stat();

                code.emit(OpCode.GOto, startLabel);
                code.emitLabel(endLabel);
                break;
            }
            case Tag.IF: {
                int elseLabel = code.newLabel();

                match(look.tag);
                match('(');
                bexpr(elseLabel);
                match(')');
                stat();
                statelse(elseLabel);
                break;
            }
            case '{':
                match(look.tag);
                statlist();
                match('}');
                break;
            default:
                error("Unexpected token in <stat>: " + look.tag);
        }
    }

    private void statfor(int startLabel, int endLabel) {
        switch (look.tag) {
            case Tag.ID:
                Word word = (Word) look;

                match(look.tag);
                match(Tag.INIT);

                int address = st.lookupAddress(word.lexeme);
                if (address == -1) {
                    address = count;
                    st.insert(word.lexeme, count++);
                }

                expr();

                code.emit(OpCode.istore, address);
                match(';');
                code.emitLabel(startLabel);
                bexpr(endLabel);
                break;
            case Tag.RELOP:
                code.emitLabel(startLabel);
                bexpr(endLabel);
                break;
            default:
                error("Unexpected token in <statfor>: " + look.tag);
        }
    }

    private void statelse(int elseLabel) {
        switch (look.tag) {
            case Tag.ELSE: {
                match(look.tag);

                int endLabel = code.newLabel();
                code.emit(OpCode.GOto, endLabel);
                code.emitLabel(elseLabel);
                
                stat();
                match(Tag.END);

                code.emitLabel(endLabel);
                break;
            }
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
                idlist(false);
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
                idlist(false);
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

    private void idlist(boolean read) {
        switch (look.tag) {
            case Tag.ID:
                if (read)
                    code.emit(OpCode.invokestatic, 0);
                else
                    code.emit(OpCode.dup);

                int address = st.lookupAddress(((Word) look).lexeme);
                if (address == -1) {
                    address = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(look.tag);

                code.emit(OpCode.istore, address);
                idlistp(read);
                break;
            default:
                error("Unexpected token in <idlist>: " + look.tag);
        }
    }

    private void idlistp(boolean read) {
        switch (look.tag) {
            case ',':
                match(',');
                if (read)
                    code.emit(OpCode.invokestatic, 0);
                else
                    code.emit(OpCode.dup);

                int address = st.lookupAddress(((Word) look).lexeme);
                if (address == -1) {
                    address = count;
                    st.insert(((Word) look).lexeme, count++);
                }
                match(look.tag);

                code.emit(OpCode.istore, address);
                idlistp(read);
                break;
            case ']':
            case ')':
                // Ɛ
                if(!read)
                    code.emit(OpCode.pop);
                break;
            default:
                error("Unexpected token in <idlistp>: " + look.tag);
        }
    }

    private void bexpr(int endLabel) {
        switch (look.tag) {
            case Tag.RELOP:
                Word word = (Word) look;

                match(Tag.RELOP);
                expr();
                expr();

                // Exercise 5.3
                switch (word.lexeme) {
                    case "<":
                        code.emit(OpCode.if_icmpge, endLabel);
                        break;
                    case ">":
                        code.emit(OpCode.if_icmple, endLabel);
                        break;
                    case "==":
                        code.emit(OpCode.if_icmpne, endLabel);
                        break;
                    case "<=":
                        code.emit(OpCode.if_icmpgt, endLabel);
                        break;
                    case "<>":
                        code.emit(OpCode.if_icmpeq, endLabel);
                        break;
                    case ">=":
                        code.emit(OpCode.if_icmple, endLabel);
                        break;
                }
                
                break;
            default:
                error("Unexpected token in <bexpr>: " + look.tag);
        }
    }

    private void expr() {
        char operation = ' ';

        switch (look.tag) {
            case '+':
            case '*':
                operation = (char) look.tag;
                match(look.tag);
                match('(');
                exprlist(false, operation);
                match(')');
                break;
            case '-':
            case '/':
                operation = (char) look.tag;
                match(look.tag);
                expr();
                expr();
                code.emit(operation == '-' ? OpCode.isub : OpCode.idiv);
                break;
            case Tag.NUM:
                code.emit(OpCode.ldc, ((NumberTok) look).lexeme);
                match(look.tag);
                break;
            case Tag.ID:
                int address = st.lookupAddress(((Word) look).lexeme);

                if (address == -1) 
                    error("ID not found");

                code.emit(OpCode.iload, address);
                match(look.tag);
                break;
            default:
                error("Unexpected token in <expr>: " + look.tag);
        }
    }

    private void exprlist(boolean print, char operation) {
        switch (look.tag) {
            case '+':
            case '*':
            case '-':
            case '/':
            case Tag.NUM:
            case Tag.ID:
                expr();

                if (print)
                    code.emit(OpCode.invokestatic, 1);

                exprlistp(print, operation);
                break;
            default:
                error("Unexpected token in <exprlist>: " + look.tag);
        }
    }

    private void exprlistp(boolean print, char operation) {
        switch (look.tag) {
            case ',':
                match(look.tag);
                expr();

                if (print)
                    code.emit(OpCode.invokestatic, 1);

                exprlistp(print, operation);

                if(operation == '+' || operation == '*')
                    code.emit(operation == '+' ? OpCode.iadd : OpCode.imul);

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
        String path = "utils/test/test.lft";

        try {
            // create a BufferedReader to read characters from the file
            BufferedReader buffer = new BufferedReader(new FileReader(path));
            // create a new Valutatore instance
            Translator T = new Translator(L, buffer);

            // start scanning
            T.start();

            // close the BufferedReader to release resources
            buffer.close();
        } catch (IOException E) {
            // handle IOException by printing the stack trace
            E.printStackTrace();
        }
    }
}
