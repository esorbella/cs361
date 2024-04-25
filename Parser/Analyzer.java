
import java.util.*;
import java.util.regex.*;
import java.io.*;


public class Analyzer {
    //maintain single list of tokens for a given program.
    public static ArrayList<Token> tokenList = new ArrayList<>();

    //regular expressions for each type of token.

    public static final String keywords = "\\b(if|else|int|double|float|struct|char|long|for|while|return|switch|case)\\b";
    public static final String strings = "\"(?:[^\"\\\\]|\\\\.)*\"";
    public static final String identifiers = "[a-zA-Z_][a-zA-Z0-9_]*";
    public static final String operators = "\\+|-|\\*|/|%|=|==|!=|<|>|<=|>=|&&|\\|\\|";
    public static final String punctuation = "[{}()\\[\\],;.]";
    public static final String constants = "\\b(\\d+\\.\\d+|\\d+\\.\\d*[eE][+-]?\\d+|\\d+[eE][+-]?\\d+|\\d+|\"[^\"]*\"|'[^']*')\\b";
    public static final String escapes = "\\\\.";
    public static final String headers = "#include\\s+<[^>]+>|#include\\s+\"[^\"]+\"";
    
    public static ArrayList<Token> Tokenizer(String input) {   
        //tokenList.clear(); // Clear the token list at the beginning
        String combinedPattern = String.join("|", keywords, identifiers, operators, punctuation, constants, escapes, headers, strings);
    
        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(input);
    
        while(matcher.find()) {   
            String tokenType = "";
            String tokenValue = matcher.group().trim(); // Trim to remove leading/trailing whitespace
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
            } else if (tokenValue.matches(headers)) {
                tokenType = "Header";
            } else if (tokenValue.matches(strings)) {
                tokenType = "String";
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

        String file = args[0];
        BufferedReader reader;
        try {
            String line;
            reader = new BufferedReader(new FileReader(file));
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