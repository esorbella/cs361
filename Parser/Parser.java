import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Parser extends Analyzer {
    private List<Token> tokens;
    private int position;
    private Map<String, String> symbolTable;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
        this.symbolTable = new HashMap<>();
    }
    
    // Parse the entire program
    public void parseProgram() {
        while (!isAtEnd()) {
            parseStatement();
        }
    }

    private void parseStatement() {
    Token token = peek();
    if (isFunctionDeclaration()) {
        parseFunctionDeclaration();
    } else if (token.getType().equals("Keyword") && token.getValue().equals("int") || token.getType().equals("Keyword") && token.getValue().equals("double")) {
        parseVariableDeclaration();
    } else if (token.getType().equals("Identifier") && (position + 1 < tokens.size()) && tokens.get(position + 1).getValue().equals("(")) {
        // If the next token is an opening parenthesis, it's a function call
        parseFunctionCall(token);
    } else if (token.getType().equals("Keyword") && token.getValue().equals("if")) {
        // If it's an "if" statement, parse the conditional expression and the block
        consume("Keyword", "if");
        consume("Punctuation", "(");
        parseExpression();
        consume("Punctuation", ")"); // Ensure to consume the closing parenthesis after the conditional expression
        parseBlock(); // Parse the main block

        // Check for an optional "else" block
        if (peek().getType().equals("Keyword") && peek().getValue().equals("else")) {
            consume("Keyword", "else");
            parseBlock(); // Parse the else block
        }
    } else if (token.getType().equals("Keyword") && token.getValue().equals("return")) {
        // If it's a "return" statement, parse the expression (if present)
        consume("Keyword", "return");
        if (!peek().getValue().equals(";")) {
            parseExpression();
        }
        Token semicolon = consume("Punctuation", ";"); // Expect a semicolon after the return statement
        System.out.println("Parsed return statement");
    } else {
        // If none of the above, it could be an assignment statement
        parseAssignmentStatement();
    }
}

  // Parse an expression
    private Token parseExpression() {
        Token token = peek();
        if (token.getType().equals("Constant")) {
            consume("Constant", null);
            System.out.println("Parsed constant expression with value " + token.getValue());
        } else if (token.getType().equals("Identifier")) {
            String varName = token.getValue();
            if (!symbolTable.containsKey(varName)) {
                throw new SemanticException("Undeclared variable '" + varName + "'");
            }
            consume("Identifier", null);
            System.out.println("Parsed identifier expression with value " + varName);
        } else if (token.getType().equals("String")) {
            consume("String", null);
            System.out.println("Parsed string literal: " + token.getValue());
        } else if (token.getType().equals("Operator") && token.getValue().equals("+")) {
            consume("Operator", "+");
            parseExpression();
            parseExpression();
            System.out.println("Parsed addition expression");
        } else {
            throw new SemanticException("Unsupported expression");
        }
        return token;
    }

    // Parse an assignment statement
    private void parseAssignmentStatement() {
        Token identifier = consume("Identifier", null);
        
        // Check if the identifier has been declared
        if (!symbolTable.containsKey(identifier.getValue())) {
            throw new SemanticException("Variable '" + identifier.getValue() + "' not declared");
        }
        
        consume("Operator", "="); // Consume the assignment operator
        Token expressionToken = parseExpression(); // Parse the expression on the right-hand side
        
        // Ensure that the remaining tokens in the assignment statement are correctly matched
        while (!peek().getValue().equals(";")) {
            // Consume the next token
            Token nextToken = peek();
            if (nextToken.getType().equals("Operator")) {
                consume("Operator", nextToken.getValue());
            } else if (nextToken.getType().equals("Identifier")) {
                consume("Identifier", nextToken.getValue());
            } else if (nextToken.getType().equals("Constant")) {
                consume("Constant", nextToken.getValue());
            } else {
                throw new SemanticException("Unexpected token in assignment statement");
            }
        }
        
        // Check for type mismatch
        if (!isTypeMatch(identifier, expressionToken)) {
            throw new SemanticException("Type mismatch: " + symbolTable.get(identifier.getValue()) + " and " + symbolTable.get(expressionToken.getValue()));
        }
    
        Token semicolon = consume("Punctuation", ";"); // Expect a semicolon after the assignment statement
        System.out.println("Parsed assignment statement for " + identifier.getValue());
    }
    

    // Check if the type of the identifier matches the type of the expression
    private boolean isTypeMatch(Token identifier, Token expressionToken) {
        //System.out.println(identifier.getValue());
        if(expressionToken.getType().equals("Constant"))
            return true;
        if (symbolTable.get(identifier.getValue()).equals(symbolTable.get(expressionToken.getValue())))
            return true;
        return false;
    }
    
    // Parse a block of statements
    private void parseBlock() {
        // Parse the block until a closing brace '}'
        consume("Punctuation", "{");
        while (!peek().getValue().equals("}")) {
            parseStatement();
        }
        consume("Punctuation", "}"); // End of the block
    }

    private boolean isFunctionDeclaration() {
        int currentPosition = position; // Save current position
        try {
            // Check if the next tokens match the pattern for a function declaration
            Token currentToken = tokens.get(currentPosition);
            Token nextToken = tokens.get(currentPosition + 1);
            Token thirdToken = tokens.get(currentPosition + 2);

            return currentToken.getType().equals("Keyword") &&
                    currentToken.getValue().equals("int") &&
                    nextToken.getType().equals("Identifier") &&
                    thirdToken.getType().equals("Punctuation") &&
                    thirdToken.getValue().equals("(");
        } catch (IndexOutOfBoundsException e) {
            // If there aren't enough tokens to check, it's not a function declaration
            return false;
        } finally {
            position = currentPosition; // Restore the position
        }
    }

    // Parse a variable declaration (e.g., "int x = 5;")
    private void parseVariableDeclaration() {
        String type = consume("Keyword", null).getValue(); // Accept any type (int or double)
        Token identifier = consume("Identifier", null);

        // Check for duplicate variable declaration
        if (symbolTable.containsKey(identifier.getValue())) {
            throw new SemanticException("Variable '" + identifier.getValue() + "' already declared");
        }

        symbolTable.put(identifier.getValue(), type);

        // Check for optional assignment
        if (peek().getValue().equals("=")) {
            consume("Operator", "=");
            parseExpression();
        }

        consume("Punctuation", ";");

        System.out.println("Parsed variable declaration for " + symbolTable.get(identifier.getValue()) + " " + identifier.getValue());
    }

    // Parse a function declaration
    private void parseFunctionDeclaration() {
        consume("Keyword", null); // Accept any return type
        Token identifier = consume("Identifier", null);
        consume("Punctuation", "(");

        // Parse parameters
        while (!peek().getValue().equals(")")) {
            parseParameter();
            // Check for comma to separate parameters
            if (peek().getValue().equals(",")) {
                consume("Punctuation", ",");
            }
        }

        consume("Punctuation", ")"); // End of parameter list
        consume("Punctuation", "{"); // Start of function body

        // Parse function body until closing brace '}'
        while (!peek().getValue().equals("}")) {
            parseStatement();
        }

        consume("Punctuation", "}"); // End of function body

        System.out.println("Parsed function declaration for " + identifier.getValue());
    }


    // Parse a function parameter
    private void parseParameter() {
        // For simplicity, let's assume parameters are of the form "Type Identifier"
        consume("Keyword", null); // Accept any type
        consume("Identifier", null);
    }

    // Parse a function call
    private void parseFunctionCall(Token identifierToken) {
        consume("Identifier", identifierToken.getValue()); // Consume the function identifier
        consume("Punctuation", "("); // Expect opening parenthesis for function call

        // Parse arguments (if any)
        while (!peek().getValue().equals(")")) {
            if (peek().getType().equals("Escape Character")) {
                // If an escape character is encountered, consume it and continue
                parseEscapeCharacter();
            } else {
                parseExpression();
            }
            // Check for comma to separate arguments
            if (peek().getValue().equals(",")) {
                consume("Punctuation", ",");
            }
        }

        consume("Punctuation", ")"); // Expect closing parenthesis for function call
        Token semicolon = consume("Punctuation", ";"); // Expect a semicolon after a function call
        System.out.println("Consumed semicolon: " + semicolon);
    }

    // Parse an escape character
    private void parseEscapeCharacter() {
        Token escapeChar = consume("Escape Character", null); // Consume the escape character
        System.out.println("Parsed escape character with value " + escapeChar.getValue());
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

    static class SemanticException extends RuntimeException {
        public SemanticException(String message) {
            super(message);
        }
    }

    public static void main(String[] args) {
        String file = args[0];
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