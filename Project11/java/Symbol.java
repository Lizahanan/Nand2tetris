public class Symbol {

    // Enum for the kind of symbol
    public static enum Kind {
        FIELD, VAR, STATIC, ARG, NONE
    }
    // Instance variables
    private String type;
    private Kind kind;
    private int index;

    /**
     * Constructor for the Symbol class
     * @param type
     * @param kind
     * @param index
     */
    public Symbol(String type, Kind kind, int index){ 
        this.type = type;
        this.kind = kind;
        this.index = index;

    }

    /**
     * Get the type of the symbol
     * @return
     */
    public String getType(){
        return this.type;
    }

    /**
     * Get the kind of the symbol
     * @return
     */

    public Kind getKind(){
        return this.kind;
    }

    /**
     * Get the index of the symbol
     * @return
     */

    public int getIndex(){
        return this.index;
    }

    @Override
    public String toString() {
        return "Symbol{" +
                "type='" + type + '\'' +
                ", kind=" + kind +
                ", index=" + index +
                '}';
    }

}
