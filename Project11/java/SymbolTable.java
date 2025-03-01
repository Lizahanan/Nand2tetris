import java.util.HashMap;
import java.util.Map;

/**
 * The SymbolTable class is used to store the symbols of the Jack language program
 * Each symbol has a scope from which it is accessible in the source code
 * Symbol table implements that by giving index (running count) to each symbol in the scope
 * The index starts at 0 and increments by 1 for each new symbol and resets to 0 when starting a new scope 
 * The class level symbols are stored in the classSymbols map -> static and field variables
 * The subroutine level symbols are stored in the subroutineSymbols map -> arg and local variables
 * When compiling error-free Jack code, any identifier not found in the symbol table may be assumed to be a subroutine name or a class name.
 * Since the Jack language syntax rules suffice for distinguishing between these two possibilities, 
 * and since no “linking” needs to be done by the compiler,
 * there is no need to keep these identifiers in the symbol table.
 */

public class SymbolTable {

    //maps to store the symbols for the class and subroutine level
    private Map<String, Symbol> classSymbols; // class level symbols STATIC, FIELD
    private Map<String, Symbol> subroutineSymbols; // for ARG, VAR
    private HashMap<Symbol.Kind,Integer> indices;

    /**
     * creates a new symbol table
     * and initializes the counters for the different categories of variables
     */
    public SymbolTable() {
        classSymbols = new HashMap<String, Symbol>();
        subroutineSymbols = new HashMap<String, Symbol>();

        indices = new HashMap<Symbol.Kind, Integer>();
        indices.put(Symbol.Kind.ARG,0);
        indices.put(Symbol.Kind.FIELD,0);
        indices.put(Symbol.Kind.STATIC,0);
        indices.put(Symbol.Kind.VAR,0);

    }

    /**
     * Starts a new subroutine scope (i.e., resets the subroutine’s symbol table)
     */
    public void startSubroutine() {
        subroutineSymbols.clear();
        indices.put(Symbol.Kind.ARG,0);
        indices.put(Symbol.Kind.VAR,0);
    }

    /**
     * Defines a new identifier and assigns it a running index
     * depending on its kind: STATIC, FIELD, ARG, or VAR.
     * @param name
     * @param type
     * @param kind
     * 
     */

    public void define(String name, String type, Symbol.Kind kind) {
        //define subroutine level symbol
        if(kind == Symbol.Kind.ARG || kind == Symbol.Kind.VAR){
            int index = indices.get(kind);
            Symbol symbol = new Symbol(type, kind, index);
            indices.put(kind, index+1);
            subroutineSymbols.put(name, symbol);
        }
        //define class level symbol
        else if(kind == Symbol.Kind.STATIC || kind == Symbol.Kind.FIELD){
            int index = indices.get(kind);
            Symbol symbol = new Symbol(type, kind, index);
            indices.put(kind, index+1);
            classSymbols.put(name, symbol);
        }
    }

    /**
     * Returns the number of variables of the given kind already defined in the current scope
     * @param kind
     */
    public int varCount(Symbol.Kind kind) {
        return indices.get(kind);
    }

    /**
     * returns the kind of the named identifier in the current scope
     * if the identifier is unknown in the current scope returns NONE
     * @param name
     * @return
     */
    public Symbol.Kind kindOf(String name) {
        Symbol symbol = lookUp(name);
        if(symbol != null){
            return symbol.getKind();
        }
        return Symbol.Kind.NONE;

    }

    /**
     * returns the type of the named identifier in the current scope
     * @param name
     * @return
     */
    public String typeOf(String name){
        Symbol symbol = lookUp(name);
        if(symbol != null){
            return symbol.getType();
        }
        return null;
    }

    /**
     * returns the index assigned to the named identifier
     * @param name
     * @return
     */
    public int indexOf(String name){

        Symbol symbol = lookUp(name);

        if (symbol != null) return symbol.getIndex();

        return -1;
    }

    /**
     * Looks up the symbol in the subroutine and class level symbols
     * @param name
     */
    private Symbol lookUp(String name){
        if (classSymbols.get(name) != null){
            return classSymbols.get(name);
        }else if (subroutineSymbols.get(name) != null){
            return subroutineSymbols.get(name);
        }else {
            return null;
        }
    }
}
