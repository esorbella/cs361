package cs361.Analyzer;
import java.util.*;
import java.util.regex.*;
import java.io.*;

public class Analyzer {
    //maintain single list of tokens for a given program.
    public static ArrayList<Token> tokenList = new ArrayList<>();

    //regular expressions for each type of token.
    public static final String keywords = "\\b(if|else|int|double|float|struct|char|long|for|while|return|switch|case)\\b";
    public static final String identifiers = "[a-zA-Z_][a-zA-Z0-9_]*";
    public static final String operators = "\\+|-|\\*|/|%|=|==|!=|<|>|<=|>=|&&|\\|\\|";
    public static final String punctuation = "[{}()\\[\\],;.]";
    public static final String constants = "\\b(\\d+\\.\\d+|\\d+\\.\\d*[eE][+-]?\\d+|\\d+[eE][+-]?\\d+|\\d+|'[^']*'|\"[^\"]*\")\\b";
    public static final String escapes = "\\\\.";

    //create Token class for each token found in
    //given program. Can return its type and value
    //and contains a toString() method.
    public static class Token{
        private String type;
        private String value;

        public Token(String type, String value)
        {
            this.type = type;
            this.value = value;
        }
        public String getType()
        {
            return this.type;
        }
        public String getValue()
        {
            return this.value;
        }
        @Override
        public String toString() {
            return "TYPE: " + this.type + " VALUE: " + this.value + "\n";
        }
    }

    //tokenize the program line-by-line.
    //each token is identified by comparing against each regular expression.
    public static ArrayList<Token> Tokenizer(String input)
    {   
        String combinedPattern = String.join("|", keywords, identifiers, operators, 
        punctuation, constants, escapes);
        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(input);

        while(matcher.find())
        {   
            String tokenType = "";
            String tokenValue = matcher.group();
            if (tokenValue.matches(keywords)) {
                tokenType = "Keyword";
            } else if (tokenValue.matches(identifiers)) {
                tokenType = "Identifier";
            } else if (tokenValue.matches(operators)) {
                tokenType = "Operator";
            } else if (tokenValue.matches(punctuation)) {
                tokenType = "Punctuation";
            } else if (tokenValue.matches(constants)) {
                tokenType = "Constant";
            } else if (tokenValue.matches(escapes)) {
                tokenType = "Escape Character";
            } else {
                System.out.println("Invalid token type for: " + tokenValue);
            }

            //add each token to overall token list
            tokenList.add(new Token(tokenType, tokenValue));
        }
        return tokenList;
    }

    //Main function. Reads file "example.c" and parses each line
    //through Tokenizer.
    public static void main(String[] args)
    {
        BufferedReader reader;
        try {
            String line;
            reader = new BufferedReader(new FileReader("example.c"));
            while ((line = reader.readLine()) != null)
            {
                Tokenizer(line);
            }
            reader.close();
        }
        catch (IOException e) {
            System.out.println("File not found.");
        }
        for (Token token: tokenList)
        {
            System.out.print(token);
        }
    }
}