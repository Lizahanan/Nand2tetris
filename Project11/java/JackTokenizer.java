import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The JackTokenizer class is used to tokenize the input Jack language program
 * 1. Removes all comments and white spaces from the input stream
 * 2. Breaks it into Jack-language tokens, as specified by the Jack grammar
 */
public class JackTokenizer {
    //enum for token types
    public static enum TokenType {
        KEYWORD, SYMBOL, IDENTIFIER, INT_CONST, STRING_CONST, NONE
    }
    //enum for keywords
    public static enum Keyword{
        CLASS, CONSTRUCTOR, FUNCTION, METHOD,
        FIELD, STATIC, VAR , INT, CHAR , BOOLEAN, VOID,
        TRUE, FALSE, NULL, THIS, LET, DO, IF, ELSE, WHILE, RETURN
    }
    
    //current token
    private String currentToken;
    // the type of current token
    private TokenType currentTokenType;
    private int pointer; 
    
    private ArrayList<String> tokens; // list of tokens

    private static Pattern tokenPatterns; // regex pattern for tokens
    private static String keyWordReg; // regex pattern for keywords
    private static String symbolReg; // regex pattern for symbols
    private static String intReg; // regex pattern for integers
    private static String strReg; // regex pattern for strings
    private static String idReg; // regex pattern for identifiers

    //hash map to store the keywords
    private static HashMap<String,Keyword> keyWordMap = new HashMap<String, Keyword>();
    //hash map to store the operators 
    private static HashSet<Character> opSet = new HashSet<Character>();

    static{
        keyWordMap.put("class",Keyword.CLASS);
        keyWordMap.put("constructor",Keyword.CONSTRUCTOR);
        keyWordMap.put("function",Keyword.FUNCTION);
        keyWordMap.put("method",Keyword.METHOD);
        keyWordMap.put("field",Keyword.FIELD);
        keyWordMap.put("static",Keyword.STATIC);
        keyWordMap.put("var",Keyword.VAR);
        keyWordMap.put("int",Keyword.INT);
        keyWordMap.put("char",Keyword.CHAR);
        keyWordMap.put("boolean",Keyword.BOOLEAN);
        keyWordMap.put("void",Keyword.VOID);
        keyWordMap.put("true",Keyword.TRUE);
        keyWordMap.put("false",Keyword.FALSE);
        keyWordMap.put("null",Keyword.NULL);
        keyWordMap.put("this",Keyword.THIS);
        keyWordMap.put("let",Keyword.LET);
        keyWordMap.put("do",Keyword.DO);
        keyWordMap.put("if",Keyword.IF);
        keyWordMap.put("else",Keyword.ELSE);
        keyWordMap.put("while",Keyword.WHILE);
        keyWordMap.put("return",Keyword.RETURN);

        opSet.add('+');
        opSet.add('-');
        opSet.add('*');
        opSet.add('/');
        opSet.add('&');
        opSet.add('|');
        opSet.add('<');
        opSet.add('>');
        opSet.add('=');
    }


    /**
     * Constructor for the JackTokenizer class
     * opens the input file/stream and gets ready to tokenize it
     * @param input
     * @throws FileNotFoundException
     */
    public JackTokenizer(File input) throws FileNotFoundException {
       try(Scanner scanner = new Scanner(input)){
        //create the scanner to read the input file line by line
        String preprocessed = ""; //holds the preprocessed jack input 
        String line = ""; //holds the current line
        //process the input file line by line
        while(scanner.hasNextLine()){
            //remove comments and white spaces
            line = removeComments(scanner.nextLine().trim());

            //only add non empty lines to the preprocessed string
            if(line.length() > 0){
                preprocessed += line + "\n";
            }
        }
        //remove all block comments
        preprocessed = removeBlockComments(preprocessed).trim();

        //init all regex
        initRegEx();

        //match the tokens using the regex pattern
        Matcher match = tokenPatterns.matcher(preprocessed);
        tokens = new ArrayList<String>();
        pointer = 0;
        //add all the tokens to the list
        while(match.find()){
            tokens.add(match.group());
        }
       } catch (FileNotFoundException e) {
           e.printStackTrace();
       }
       //initialize the current token and type
       currentToken = "";
       currentTokenType = TokenType.NONE;
    }

    
    /**
     * initialise the regular expressions
     * for the different types of tokens
     */
    private void initRegEx(){
        keyWordReg ="";
        for(String key : keyWordMap.keySet()){
            keyWordReg += key + "|";
        }
       // Regex to match Jack symbols
        symbolReg = "[\\{\\}\\[\\]\\(\\)\\.\\,\\;\\+\\-\\*\\/\\&\\|\\<\\>\\=\\~]";
        intReg = "[0-9]+";
        strReg = "\"[^\"\n]*\"";
        idReg = "[\\w_][\\w\\d_]*";
        tokenPatterns = Pattern.compile(idReg + "|" + keyWordReg + symbolReg + "|" + intReg + "|" + strReg);

    }

    /**
     * check if we have more tokens in the input using our pointer
     * @return
     */
   

    public boolean hasMoreTokens() {
        return pointer < tokens.size();
    }

    /**
     * get the next token from the input
     * make it the current token 
     * should only be called if there are more tokens 
     * initially there is no current token
     */

    public void advance()  {
        if (hasMoreTokens()){
            currentToken = tokens.get(pointer); //get the current token
            pointer++; //move the pointer to the next token
        } else {
            throw new IllegalStateException("No more tokens");
        }
        System.out.println("Current Token: " + currentToken);

        if(currentToken.matches(keyWordReg)){
            currentTokenType = TokenType.KEYWORD;
        } else if (currentToken.matches(symbolReg)){
            currentTokenType = TokenType.SYMBOL;
        } else if (currentToken.matches(intReg)){
            currentTokenType = TokenType.INT_CONST;
        } else if (currentToken.matches(strReg)){
            currentTokenType = TokenType.STRING_CONST;
        } else if (currentToken.matches(idReg)){
            currentTokenType = TokenType.IDENTIFIER;
        } else {
            throw new IllegalArgumentException("Token not recognized: " + currentToken);
        }     
    }
    

    /**
     * gets current token 
     * @return
     */
    public String getCurrentToken() {
        return currentToken;
    }
    /**
     * Returns the type of the current token
     * @return
     */
    public TokenType tokenType(){

        return currentTokenType;
    }


    /**
     * Return the keyword -> current token 
     * only used if current token is keyword
     * @return
     */
    public Keyword keyWord(){
        if(currentTokenType != TokenType.KEYWORD){
            throw new IllegalStateException("Current token is not a keyword");
        } 

        return keyWordMap.get(currentToken);
    }

    /**
     * Returns the character which is the current token
     * only used if current token is symbol
     * @return
     */
    public char symbol() {
        if(currentTokenType != TokenType.SYMBOL){
            throw new IllegalStateException("Current token is not a symbol");
        } 

        return currentToken.charAt(0);
    }

    /**
     * Returns the identifier which is the current token
     * only used if current token is identifier
     * @return
     */
    public String identifier() {
        if(currentTokenType != TokenType.IDENTIFIER){
            throw new IllegalStateException("Current token is not an identifier");
        } 

        return currentToken;
    }

    /**
     * Returns the integer value of the current token
     * only used if current token is integer constant
     * @return
     */
    public int intVal() {
       if(currentTokenType != TokenType.INT_CONST){
            throw new IllegalStateException("Current token is not an integer constant");
        } 

        return Integer.parseInt(currentToken);
    }

    /**
     * Returns the string value of the current token
     * only used if current token is string constant
     * @return
     */

    public String stringVal() {
        if(currentTokenType != TokenType.STRING_CONST){
            throw new IllegalStateException("Current token is not a string constant");
        } 

        return currentToken.substring(1,currentToken.length()-1);
    }

    /**
     * method to move the pointer back to the previous token
     */
     public void moveBack(){
        if(pointer > 0){
            pointer--;
            currentToken = tokens.get(pointer);
        } else {
            throw new IllegalStateException("No previous token");
        }
     }

    /**
     * return if current symbol is an operator
     * 
     */
    public boolean isOp(){
        return opSet.contains(symbol());
    }

    /**
     * remove all comments from the input
     * @param line
     * @return
     */
    private String removeComments(String line){
        int commPos = line.indexOf("//");
        if(commPos != -1){
            return line.substring(0,commPos);
        } else {
            return line;
        }
    }

    /**
     * remove all block comments from the input
     * @param input
     * @return
     */
    public String removeBlockComments(String input){

        int start = input.indexOf("/*");
        if (start == -1) {
            return input;
        }
        String result = input;
        int end = input.indexOf("*/");
        while (start != -1){
            if (end == -1){
                return result.substring(0,start - 1);
            }
            result = result.substring(0,start) + result.substring(end+2);
            start = result.indexOf("/*");
            end = result.indexOf("*/");
        }
        return result;
    }
}
