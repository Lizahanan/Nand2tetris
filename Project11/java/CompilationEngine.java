import java.io.*;
import java.util.Arrays;


public class CompilationEngine {
    private JackTokenizer tokenizer;
    private CodeWriter writer;
    private SymbolTable symbolTable;
    private String currentClass;
    private String currentSubroutine;
    private int labelInd;

    /**
     * create new compilation engine
     * the next routine called must be compile class
     * @param input
     * @param output
     * @throws IOException
     */

    public CompilationEngine(File inFile, File outFile) throws IOException {
        tokenizer = new JackTokenizer(inFile); // Create a new JackTokenizer
        writer = new CodeWriter(outFile); // Create a new CodeWriter
        symbolTable = new SymbolTable(); // Create a new SymbolTable
        
        labelInd = 0; // Initialize the label index
    }


    /**
     * returns the current function name
     * @return
     */
    private String currentFunction(){
        if (currentClass.length() != 0 && currentSubroutine.length() !=0){
            return currentClass + "." + currentSubroutine;
        }
        return "";
    }

   

    /**
     * compiles a type 
     * @return type
     * 
     */

    private String compileType(){
        tokenizer.advance();

        //check if the type is int, char, boolean or a class name
        if(tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD && (tokenizer.keyWord() == JackTokenizer.Keyword.INT || 
        tokenizer.keyWord() == JackTokenizer.Keyword.CHAR || tokenizer.keyWord() == JackTokenizer.Keyword.BOOLEAN)){
            return tokenizer.getCurrentToken();
        }

        if(tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER){
            return tokenizer.identifier();
        }
        error("int|char|boolean|className");
        return "";

    }

    /**
     * throw an exception to report errors
     * @param val
     */
    private void error(String val){
        throw new IllegalStateException("Expected token missing : " + val + " Current token:" + tokenizer.getCurrentToken());
    }
    
    /**
     * processes a symbol which we require to have a specific value
     * @throws IOException
     */
    private void process(char symbol) throws IOException {
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TokenType.SYMBOL || tokenizer.symbol() != symbol){
            System.out.println("Expected symbol: " + symbol + " but got: " + tokenizer.getCurrentToken());
        }
    }

    /**
     * Compiles a complete class 
     * 'class' className '{' classVarDec* subroutineDec* '}'
     * @throws IOException
     */
    public void compileClass() throws IOException {
        System.out.println("Compiling class");
        //move to the 'class' keyword
        tokenizer.advance();
        //check if the current token is 'class'
        if(tokenizer.tokenType() != JackTokenizer.TokenType.KEYWORD || tokenizer.keyWord() != JackTokenizer.Keyword.CLASS){
            System.out.println(tokenizer.getCurrentToken());
            error("class");
        }
        //move to the class name
        tokenizer.advance();
        //check if the current token is an identifier
        if(tokenizer.tokenType() != JackTokenizer.TokenType.IDENTIFIER){
            error("className");
        }

        //class name doesnt have to be stored in the symbol table but we store it for future use and debugging
        currentClass = tokenizer.identifier();
        process('{');

        //compile class variable declarations
        compileClassVarDec();
        //compile subroutine declarations
        compileSubroutine();

        process('}');

        if (tokenizer.hasMoreTokens()){
            throw new IllegalStateException("Unexpected tokens");
        }

        writer.close();
 
    }

    public void compileClassVarDec() throws IOException {
        System.out.println("Compiling class variable declaration");
        //current token is '{'
        //advance to the next token
        tokenizer.advance();
        //check if it's a '}'
        if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '}'){
            tokenizer.moveBack(); //move back to the previous token and come back to the caller
            return;
        }
        //check if its a keyword 
        if(tokenizer.tokenType() != JackTokenizer.TokenType.KEYWORD){
            error("keyword");
        }

        //check if its a subroutineDec 
        if(tokenizer.keyWord() == JackTokenizer.Keyword.METHOD || 
        tokenizer.keyWord() == JackTokenizer.Keyword.FUNCTION ||
        tokenizer.keyWord() == JackTokenizer.Keyword.CONSTRUCTOR){
            tokenizer.moveBack(); //move back to the previous token and come back to the caller
            return;
        }

        //if we get to this point then it must be a class variable declaration we check
        if(tokenizer.keyWord() != JackTokenizer.Keyword.STATIC && tokenizer.keyWord() != JackTokenizer.Keyword.FIELD){
            error("static|field");
        }

        Symbol.Kind kind = null;
        String type ="";
        String name = "";

        //get the kind of the variable
        switch(tokenizer.keyWord()){
            case STATIC:
                kind = Symbol.Kind.STATIC;
                break;
            case FIELD:
                kind = Symbol.Kind.FIELD;
                break;
            default:
                error("static|field");
        }

        //get the type of the variable
        type = compileType();

        //flag to check if we are done with the variable declarations
        boolean done = false;

        do {
            //advance to the variable name 
            tokenizer.advance();
            //check if the current token is an identifier
            if(!tokenizer.tokenType().equals(JackTokenizer.TokenType.IDENTIFIER)){
                error("identifier");
            }

            //get the name of the variable
            name = tokenizer.identifier();
            //add the variable to the symbol table
            symbolTable.define(name, type, kind);

            //advance to the next token -> , or ;
            tokenizer.advance();
            //check if the current token is a comma or a ;
            if(!tokenizer.tokenType().equals(JackTokenizer.TokenType.SYMBOL) || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')){
                error(", or ;");
            }

            //if a semicolon is found then we are done with the variable declarations 
            if(tokenizer.symbol() == ';'){
                break;
            }

        } while(true);

        compileClassVarDec(); //recurse to the next variable declaration

    }

    /**
     * compiles a subroutine 
     * method function or constructor
     * @throws IOException
     */

    public void compileSubroutine() throws IOException {
        System.out.println("Compiling subroutine");
        //move to the next token 
        tokenizer.advance();
        //check if the current token is a '}'
        if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '}'){
            tokenizer.moveBack(); //move back to the previous token and come back to the caller
            return;
        }

        //check if its a subroutine keyword method function or constructor
        if(tokenizer.tokenType() != JackTokenizer.TokenType.KEYWORD ||
        (tokenizer.keyWord() != JackTokenizer.Keyword.METHOD && tokenizer.keyWord() != JackTokenizer.Keyword.FUNCTION && tokenizer.keyWord() != JackTokenizer.Keyword.CONSTRUCTOR)){
            error("method|function|constructor");
        }

        JackTokenizer.Keyword keyword = tokenizer.keyWord();
        //start a new subroutine
        symbolTable.startSubroutine();

        //for method "this" is the first argument 
        if(keyword == JackTokenizer.Keyword.METHOD){
            symbolTable.define("this", currentClass, Symbol.Kind.ARG);
        }
        String type = "";
        //advance to the next token -> return type
        tokenizer.advance();
        //check if it's 'void' or other type 
        if(tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD && tokenizer.keyWord() == JackTokenizer.Keyword.VOID){
            type = "void";
        } else {
            tokenizer.moveBack(); //move back to the previous token because compile type starts with advancing to the next token
            type = compileType();
        }

        //subroutine name
        tokenizer.advance();
        //check if the current token is an identifier
        if(tokenizer.tokenType() != JackTokenizer.TokenType.IDENTIFIER){
            error("subroutine name");
        }

        currentSubroutine = tokenizer.identifier();
        process('(');
        compileParameterList(); //note we start with advancing to the next token in compileParameterList
        process(')');
        //compile the subroutine body
        compileSubroutineBody(keyword);

        compileSubroutine(); //recurse to the next subroutine
        
    }

   


    /**
     * compiles a subroutine body
     * @param keyword
     * @throws IOException
     */
    public void compileSubroutineBody(JackTokenizer.Keyword keyword) throws IOException {
        System.out.println("Compiling subroutine body");
        //subroutine body starts with a {
        process('{');

        //compile the variable declarations
        compileVarDec();

        //write the VM function declaration
        writeFunctionDeclaration(keyword);

        //compile the statements
        compileStatement();

        //end of the subroutine body
        process('}');
        
    }

    /**
     * write VM function declaration and load 
     * pointer if keyword is method or constructor
     * @param keyword
     */
    private void writeFunctionDeclaration(JackTokenizer.Keyword keyword){
        writer.writeFunction(currentFunction(), symbolTable.varCount(Symbol.Kind.VAR));

        //methods and constructors need to load the pointer
        if(keyword == JackTokenizer.Keyword.METHOD){
            writer.writePush(CodeWriter.Segment.ARG, 0);
            writer.writePop(CodeWriter.Segment.POINTER, 0);
        } else if(keyword == JackTokenizer.Keyword.CONSTRUCTOR){
            writer.writePush(CodeWriter.Segment.CONST, symbolTable.varCount(Symbol.Kind.FIELD));
            writer.writeCall("Memory.alloc", 1);
            writer.writePop(CodeWriter.Segment.POINTER, 0);
        }

    }   


    /**
     * compiles a var declaration
     * 'var' type varName (',' varName)* ';'
     * @throws IOException
     */
    public void compileVarDec() throws IOException {
        System.out.println("Compiling variable declaration");
        //check if there's a var declaration
        tokenizer.advance();

        //if it's not "var " we move back 
        if(tokenizer.tokenType() != JackTokenizer.TokenType.KEYWORD || tokenizer.keyWord() != JackTokenizer.Keyword.VAR){
            tokenizer.moveBack();
            return;
        }

        //process the type of the variable int, boolean etc.
        String type = compileType();
        boolean done = false;
        do {
            tokenizer.advance();
            //check if the current token is an identifier
            if(tokenizer.tokenType() != JackTokenizer.TokenType.IDENTIFIER){
                error("identifier");
            }

            symbolTable.define(tokenizer.identifier(), type, Symbol.Kind.VAR);

            tokenizer.advance();
            //check if the current token is a comma or a semicolon
            if(tokenizer.tokenType() != JackTokenizer.TokenType.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ';')){
                error(", or ;");
            }
            //break if its a semicolon
            if(tokenizer.symbol() == ';'){
                break;
            }
        } while(true);
        compileVarDec(); //recurse to the next variable declaration   
    }

    /**
     * compiles a single statement 
     * @throws IOException
     */
    public void compileStatement() throws IOException {
        System.out.println("Compiling statement");

        //determine if its a statement 
        tokenizer.advance();

        //check if it's "}"
        if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '}'){
            tokenizer.moveBack(); //move back to the previous token and come back to the caller
            return;
        }

        if(tokenizer.tokenType() != JackTokenizer.TokenType.KEYWORD){
            error("keyword");
        } else {
            switch (tokenizer.keyWord()){ 
                case LET:
                    compileLet();
                    break;
                case IF:
                    compileIf();
                    break;
                case WHILE:
                    compileWhile();
                    break;
                case DO:
                    compileDo();
                    break;
                case RETURN:
                    compileReturn();
                    break;
                default:
                    error("let|if|while|do|return");  
            }
        }
        compileStatement(); //recurse to the next statement
   
    }
    /**
     * compiles a parameter list
     * possibly empty 
     * does not include the enclosing "()"
     * 
     */
    private void compileParameterList(){
        System.out.println("Compiling parameter list");

        //check if there is a parameter list
        tokenizer.advance();
        //if the next token is a closing parenthesis we move back to the caller 
        if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ')'){
            tokenizer.moveBack(); //move back to the previous token and come back to the caller
            return;
        }

        String type = "";
        tokenizer.moveBack(); //move back to the previous token because compile type starts with advancing to the next token
        do{
            type = compileType();
            tokenizer.advance();
            //check if the current token is an identifier
            if(tokenizer.tokenType() != JackTokenizer.TokenType.IDENTIFIER){
                error("identifier");
            }
            symbolTable.define(tokenizer.identifier(), type, Symbol.Kind.ARG);
            tokenizer.advance();
            //check if the current token is a comma or a semicolon
            if(tokenizer.tokenType() != JackTokenizer.TokenType.SYMBOL || (tokenizer.symbol() != ',' && tokenizer.symbol() != ')')){
                error(", or )");
            }
            //break if its a closing parenthesis
            if(tokenizer.symbol() == ')'){
                tokenizer.moveBack();
                break;
            }
        } while (true);   
    }

    /**
     * compiles a let statement 
     * 'let' varName ('[' expression ']')? '=' expression ';'
     * @throws IOException
     */

    public void compileLet() throws IOException {
        System.out.println("Compiling let statement");
        //advance to the var name 
        tokenizer.advance();
        //check if its an identifier
        if(tokenizer.tokenType() != JackTokenizer.TokenType.IDENTIFIER){
            error("varName");
        }
    
        String varName = tokenizer.identifier();  // Get variable name
        tokenizer.advance(); // Advance past the variable name

        //next is either an opening bracket for array or an equal sign
        if (tokenizer.tokenType() != JackTokenizer.TokenType.SYMBOL || (tokenizer.symbol() != '[' && tokenizer.symbol() != '=')) {
            error("[ or =");
        }
    
        boolean expression = false;
        if (tokenizer.symbol() == '['){
            expression = true;

            //push array variable base address into the stack 
            writer.writePush(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));

            //calculate the offset 
            compileExpression();

            process(']');
            //base + offset 
            writer.writeArithmetic(CodeWriter.Command.ADD);
        }
        if(expression) tokenizer.advance();

        //compile the expression 
        compileExpression();
        process(';');

        if(expression){
            //pop expression value to temp 
            writer.writePop(CodeWriter.Segment.TEMP, 0);
            //pop base + index 
            writer.writePop(CodeWriter.Segment.POINTER, 1);
            //pop expression value into *(base+index)
            writer.writePush(CodeWriter.Segment.TEMP,0);
            writer.writePop(CodeWriter.Segment.THAT,0);
        } else {
            //pop the expression value directly 
            writer.writePop(getSegment(symbolTable.kindOf(varName)), symbolTable.indexOf(varName));
        }

    }

    /**
     * return the corresponding segment 
     * @param kind
     * @return
     */
    private CodeWriter.Segment getSegment(Symbol.Kind kind){
        switch(kind){
            case STATIC:
                return CodeWriter.Segment.STATIC;
            case FIELD:
                return CodeWriter.Segment.THIS;
            case ARG:
                return CodeWriter.Segment.ARG;
            case VAR:
                return CodeWriter.Segment.LOCAL;
            default:
                return CodeWriter.Segment.NONE;
        }
    }
    
        
    
    /**
     * compiles an if statement
     * @throws IOException
     */
    public void compileIf() throws IOException {
        System.out.println("Compiling if statement");
        //generate unique labels
        String elseLabel = generateUniqueLabel();
        String endLabel = generateUniqueLabel();

        process('(');
        compileExpression();
        process(')');
        //generate VM code for if statement
        writer.writeArithmetic(CodeWriter.Command.NOT);
        writer.writeIf(elseLabel);
        process('{');
        compileStatement();
        process('}');
        writer.writeGoto(endLabel);
        writer.writeLabel(elseLabel);

        tokenizer.advance();
        if(tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD && tokenizer.keyWord() == JackTokenizer.Keyword.ELSE){
            process('{');
            compileStatement();
            process('}');
        } else {
            tokenizer.moveBack();
        }
        writer.writeLabel(endLabel);
        

    }

    public void compileWhile() throws IOException {
        System.out.println("Compiling while statement");
        
        String continueLabel = generateUniqueLabel();
        String topLabel = generateUniqueLabel();

        //write the start label
        writer.writeLabel(topLabel);
        process('(');
        compileExpression();
        process(')');
        //generate VM code for while loop
        writer.writeArithmetic(CodeWriter.Command.NOT);
        writer.writeIf(continueLabel);
        //process the body of the while loop
        process('{');
        compileStatement();
        process('}');
        
        //jump back to the start of the loop
        writer.writeGoto(topLabel);

        //or continue to the next statement
        writer.writeLabel(continueLabel);
    }

    /**
     * generate a unique label
     * @return
     */
    private String generateUniqueLabel() {

        return "LABEL_" + labelInd++;
    }

    public void compileDo() throws IOException {
        System.out.println("Compiling do statement");
        //subroutineCall
        compileSubroutineCall();
        //';'
        process(';');
        //pop return value
        writer.writePop(CodeWriter.Segment.TEMP,0);
       
    }

    /**
     * compiles a return statement
     * 'return' expression? ';'
     */
    public void compileReturn() throws IOException {
        System.out.println("Compiling return statement");
        //check if there's an expression
        tokenizer.advance();

        //if the next token is a semicolon we move back to the caller
        if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ';'){
            //no expression push 0 to the stack
            writer.writePush(CodeWriter.Segment.CONST, 0);
        } else {
            tokenizer.moveBack(); //move back to the previous token because compile expression starts with advancing to the next token
            compileExpression();
            process(';');

        }
        writer.writeReturn();
   
    }

    public void compileExpression() throws IOException {
        System.out.println("Compiling expression");
        compileTerm();
        //(op term)*
        do {
            tokenizer.advance();
            //op
            if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.isOp()){

                String opCmd = "";

                switch (tokenizer.symbol()){
                    case '+':opCmd = "add";break;
                    case '-':opCmd = "sub";break;
                    case '*':opCmd = "call Math.multiply 2";break;
                    case '/':opCmd = "call Math.divide 2";break;
                    case '<':opCmd = "lt";break;
                    case '>':opCmd = "gt";break;
                    case '=':opCmd = "eq";break;
                    case '&':opCmd = "and";break;
                    case '|':opCmd = "or";break;
                    default:error("Unknown op!");
                }

                //term
                compileTerm();

                writer.writeCommand(opCmd,"","");

            }else {
                tokenizer.moveBack();
                break;
            }

        }while (true);
    }
    

    public void compileTerm() throws IOException {
        System.out.println("Compiling term");
        tokenizer.advance();
        //check if its an identifier 
        if(tokenizer.tokenType() == JackTokenizer.TokenType.IDENTIFIER){
            String identifier = tokenizer.identifier();
            tokenizer.advance();
            //check if its an array access
            if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '['){
                //push the base address of the array
                writer.writePush(getSegment(symbolTable.kindOf(identifier)), symbolTable.indexOf(identifier));
                //calculate the offset
                compileExpression();
                process(']');
                //add the offset to the base address
                writer.writeArithmetic(CodeWriter.Command.ADD);
                //push the value of the array
                writer.writePop(CodeWriter.Segment.POINTER, 1);
                writer.writePush(CodeWriter.Segment.THAT, 0);
            } else if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && (tokenizer.symbol() == '(' || tokenizer.symbol() == '.')){
                tokenizer.moveBack();
                tokenizer.moveBack();
                compileSubroutineCall();
            } else {
                tokenizer.moveBack();
                writer.writePush(getSegment(symbolTable.kindOf(identifier)), symbolTable.indexOf(identifier));
            }
        } else if(tokenizer.tokenType() == JackTokenizer.TokenType.INT_CONST){
            writer.writePush(CodeWriter.Segment.CONST, tokenizer.intVal());
        } else if(tokenizer.tokenType() == JackTokenizer.TokenType.STRING_CONST){
            String str = tokenizer.stringVal();
            writer.writePush(CodeWriter.Segment.CONST, str.length());
            writer.writeCall("String.new", 1);
            for(int i = 0; i < str.length(); i++){
                writer.writePush(CodeWriter.Segment.CONST, str.charAt(i));
                writer.writeCall("String.appendChar", 2);
            }
        } else if(tokenizer.tokenType() == JackTokenizer.TokenType.KEYWORD){
            switch(tokenizer.keyWord()){
                case TRUE:
                    writer.writePush(CodeWriter.Segment.CONST, 0);
                    writer.writeArithmetic(CodeWriter.Command.NOT);
                    break;
                case FALSE:
                case NULL:
                    writer.writePush(CodeWriter.Segment.CONST, 0);
                    break;
                case THIS:
                    writer.writePush(CodeWriter.Segment.POINTER, 0);
                    break;
                default:
                    error("true|false|null|this");
            }
        } else if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && (tokenizer.symbol() == '(')){
            compileExpression();
            process(')');
        } else if(tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && (tokenizer.symbol() == '-' || tokenizer.symbol() == '~')){
            char a = tokenizer.symbol();
            compileTerm();
            if(a == '-'){
                writer.writeArithmetic(CodeWriter.Command.NEG);
            } else {
                writer.writeArithmetic(CodeWriter.Command.NOT);
            }
        } else {
            error("term");
        }
          
    }

    
    /**
     * compiles a subroutine call
     * subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'  
     * @throws IOException
     */
    public void compileSubroutineCall() throws IOException {
        System.out.println("Compiling subroutine call");
        tokenizer.advance();
        if (tokenizer.tokenType() != JackTokenizer.TokenType.IDENTIFIER) {
            error("identifier");
        }
    
        String name = tokenizer.identifier();
        int nArgs = 0;
    
        tokenizer.advance();
        if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '(') {
            // Push "this" pointer for method calls
            writer.writePush(CodeWriter.Segment.POINTER, 0);
            nArgs = compileExpressionList() + 1;
            process(')');
            writer.writeCall(currentClass + "." + name, nArgs);
    
        } else if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == '.') {
            String objName = name;
    
            // Get the subroutine name
            tokenizer.advance();
            if (tokenizer.tokenType() != JackTokenizer.TokenType.IDENTIFIER) {
                error("identifier");
            }
            name = tokenizer.identifier();

            if(isBuiltInClass(objName)|| objName.equals(currentClass)|| symbolTable.typeOf(objName) != null){
                name = objName + "." + name;
            } else{
                String type = symbolTable.typeOf(objName);
                if (type == null) {
                    throw new IllegalArgumentException("Undefined object '" + objName + "'");
                }
    
                if (!type.equals("int") && !type.equals("boolean") && !type.equals("char") && !type.equals("void")) {
                    nArgs = 1;
                    writer.writePush(getSegment(symbolTable.kindOf(objName)), symbolTable.indexOf(objName));
                    name = type + "." + name;
                } else {
                    name = objName + "." + name;
                }
            }
            process('(');
            nArgs += compileExpressionList();
            process(')');
            writer.writeCall(name, nArgs);
    
        } else {
            error("'(' or '.' expected");
        }
    }
    /**
    * Check if the given object name corresponds to a built-in Jack class.
        */
    private boolean isBuiltInClass(String objName) {
        return Arrays.asList("Output", "Math", "Memory", "Keyboard", "Array", "Sys","Screen").contains(objName);
    }
    

    
    
    
    

    public int compileExpressionList() throws IOException {
        System.out.println("Compiling expression list");
        int nArgs = 0;

        tokenizer.advance();
        //determine if there is any expression, if next is ')' then no
        if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ')'){
            tokenizer.moveBack();
        }else {
            nArgs = 1;
            tokenizer.moveBack();
            //expression
            compileExpression();
            //(','expression)*
            do {
                tokenizer.advance();
                if (tokenizer.tokenType() == JackTokenizer.TokenType.SYMBOL && tokenizer.symbol() == ','){
                    //expression
                    compileExpression();
                    nArgs++;
                }else {
                    tokenizer.moveBack();
                    break;
                }

            }while (true);
        }

        return nArgs;
    }
    
    
}
