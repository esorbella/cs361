import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser extends Analyzer {
    private List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    // Parse the entire program
    public void parseProgram() {
        while (!isAtEnd()) {
            parseStatement();
        }
    }

    // Parse a single statement
    private void parseStatement() {
        Token token = peek();
        if (token.getType().equals("Keyword") && token.getValue().equals("int")) {
            parseVariableDeclaration();
        } else {
            parseExpression();
            Token semicolon = consume("Punctuation", ";"); // Expect a semicolon after an expression
            System.out.println("Consumed semicolon: " + semicolon);
        }
    }

    // Parse a variable declaration (e.g., "int x = 5;")
    private void parseVariableDeclaration() {
        consume("Keyword", "int");
        Token identifier = consume("Identifier", null);
        consume("Operator", "=");
        parseExpression();
        consume("Punctuation", ";");
        System.out.println("Parsed variable declaration for " + identifier.getValue());
    }

    // Parse a function declaration (stub)
    private void parseFunctionDeclaration() {
        consume("Keyword", "void");
        Token identifier = consume("Identifier", null);
        consume("Punctuation", "(");
        // Add more parsing logic here for function parameters and body
        System.out.println("Parsed function declaration for " + identifier.getValue());
    }

    // Parse an expression (this is a simplified version)
    private void parseExpression() {
        Token token = peek();
        if (token.getType().equals("Constant")) {
            consume("Constant", null);
            System.out.println("Parsed constant expression with value " + token.getValue());
        } else if (token.getType().equals("Identifier")) {
            consume("Identifier", null);
            System.out.println("Parsed identifier expression with value " + token.getValue());
        } else {
            throw new ParseException("Expected expression, but found token: " + token);
        }
    }

    // Consume a token of a specific type and value (if value is not null)
    private Token consume(String type, String value) {
        Token token = peek();
        if (token.getType().equals(type) && (value == null || token.getValue().equals(value))) {
            position++;
            return token;
        }
        throw new ParseException("Expected token of type " + type + " with value " + value + " but got " + token);
    }

    // Peek at the current token without consuming it
    private Token peek() {
        if (isAtEnd()) {
            throw new ParseException("Reached end of token list");
        }
        return tokens.get(position);
    }

    // Check if we've reached the end of the token list
    private boolean isAtEnd() {
        return position >= tokens.size();
    }

    public static void main(String[] args) {
        String file = "example2.c";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                Tokenizer(line);
            }
            reader.close();
        } catch (IOException e) {
            System.out.println("File not found.");
        }

        Parser parser = new Parser(tokenList);
        parser.parseProgram();
    }

    // Custom exception class for parse errors
    static class ParseException extends RuntimeException {
        public ParseException(String message) {
            super(message);
        }
    }
}
