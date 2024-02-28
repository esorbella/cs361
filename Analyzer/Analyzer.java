package cs361.Analyzer;
import java.util.*;
import java.io.*;

public class Analyzer {
    //create Token class for each token found in
    //given program. Can return its type and value
    //as well as a toString() method.
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
            return "Token: | Type: " + this.type + " | Value: " + this.value;
        }
    }
    //TODO:
    //Tokenize the given program by assigning each keyword/
    //punctuation mark with a type and value
    //Example: { = punctuation
    //Example: int = keyword
    //Example + = operator... etc.
    public static ArrayList<String> Tokenizer(String input)
    {
        ArrayList<String> myList = new ArrayList<>();

        String[] data = input.split("\\s+");

        for (String value: data)
        {
            System.out.println(value);
        }

        return myList;
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
        }
        catch (IOException e) {
            System.out.println("File not found.");
        }
    }
}