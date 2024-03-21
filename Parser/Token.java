public class Token{
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


